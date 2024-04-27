package main

import (
	log "com/securithings/network-chaos/infra"
	"com/securithings/network-chaos/src/latency"
	packet_loss "com/securithings/network-chaos/src/packet-loss"
	"flag"
	"fmt"
	"os"
	"os/exec"
	"os/signal"
	"runtime"
	"strconv"
	"syscall"
	"time"
)

func main() {
	// validate OS support
	osType := runtime.GOOS
	if osType != "linux" {
		log.Log.Infof("Network Chaos service is not supported for %s OS", osType)
		log.Log.Info("Bye bye...")
		return
	}

	// Define command-line flags
	attackFlag := flag.String("a", "", "Attack name (latency | drop)")
	continuousFlag := flag.Bool("c", false, "Run the continuous chaos")
	interfaceFlag := flag.String("i", "", "Network interface name")
	srcPortFlag := flag.String("sport", "", "Source port to attack. SSH port is not supported")
	srcIpFlag := flag.String("src", "", "Source host address to attack")
	dstPortFlag := flag.String("dport", "", "Destination port to attack. SSH port is not supported")
	dstIpFlag := flag.String("dst", "", "Destination host address to attack. Must be configured with source host")
	latencyFlag := flag.String("l", "", "latency in milliseconds")
	packetLossFlag := flag.String("pl", "", "Packet loss percetage (e.g 10)")
	durationFlag := flag.String("d", "", "Attack duration in milliseconds")
	sleepBetweenAttackDurationFlag := flag.String("s", "", "sleep duration in milliseconds between attacks in case of continuous attack")

	// Parse command-line arguments
	flag.Parse()

	flag.Usage = func() {
		printHelp()
	}

	var sleepDuration int64 = -1
	if len(os.Args) < 2 {
		*attackFlag = os.Getenv("ATTACK")
		continuousEnv := os.Getenv("CONTINUOUS")
		*continuousFlag = false
		if continuousEnv != "" {
			boolValue, err := strconv.ParseBool(continuousEnv)
			if err != nil {
				fmt.Println("Error parsing continuous attack boolean value:", err)
				boolValue = false
			}
			*continuousFlag = boolValue
		}
		*interfaceFlag = os.Getenv("NETWORK_INTERFACE")
		*srcIpFlag = os.Getenv("SRC_IP_ADDRESS")
		*srcPortFlag = os.Getenv("SRC_PORT")
		*dstIpFlag = os.Getenv("DST_IP_ADDRESS")
		*dstPortFlag = os.Getenv("DST_PORT")
		*latencyFlag = os.Getenv("LATENCY")
		*packetLossFlag = os.Getenv("PACKET_LOSS_PERCENTAGE")
		*durationFlag = os.Getenv("ATTACK_DURATION")
		*sleepBetweenAttackDurationFlag = os.Getenv("CONTINUOUS_ATTACK_SLEEP_TIME")
		if *sleepBetweenAttackDurationFlag != "" {
			sleepDurationResult, err := strconv.ParseInt(*sleepBetweenAttackDurationFlag, 10, 64)
			if err != nil {
				log.Log.Error("Error parsing sleepBetweenAttackDurationFlag :", err)
				return
			}
			sleepDuration = sleepDurationResult
		}

	}

	// Set up signal handling
	signals := make(chan os.Signal, 1)
	signal.Notify(signals, syscall.SIGTERM, syscall.SIGINT)

	if *interfaceFlag != "" {
		go waitForTermination(*interfaceFlag, signals)
	}

	launchAttack(*attackFlag, *interfaceFlag, *srcPortFlag, *srcIpFlag, *dstPortFlag, *dstIpFlag, *latencyFlag, *packetLossFlag, *durationFlag, *continuousFlag, sleepDuration)
}

func launchAttack(attack string, interfaceName string, srcPort string, srcIp string, destPort string, destIp string, networkLatency string, packetLossPercentage string, attackDuration string, continuous bool, sleepDuration int64) {
	switch attack {
	case "latency":
		fmt.Println("===============================================")
		fmt.Println("=========== Chosen attack : latency ===========")
		fmt.Println("===============================================")
		if !validateLatencyAttackConfiguration(interfaceName, srcIp, destIp, srcPort, destPort, networkLatency, attackDuration) {
			printHelp()
			return
		}

		if !continuous {
			fmt.Println()
			fmt.Println("============ Starting attack ============")
			latency.Attack(interfaceName, srcIp, srcPort, destIp, destPort, networkLatency, attackDuration)
			fmt.Println("=========== Attack terminated ===========")
		} else if sleepDuration <= 0 {
			printHelp()
			return
		} else {
			for {
				fmt.Println()
				fmt.Println("============ Starting attack ============")
				err := latency.Attack(interfaceName, srcIp, srcPort, destIp, destPort, networkLatency, attackDuration)
				if err != nil {
					log.Log.Errorf("Latency attack terminated with an error: %s", err)
					return
				}
				fmt.Println("=========== Attack terminated ===========")
				fmt.Println()
				fmt.Printf("Sleeping for %v milliseconds before launching new attack", sleepDuration)
				fmt.Println()
				time.Sleep(time.Duration(sleepDuration) * time.Millisecond)
			}
		}
	case "drop":
		fmt.Println("===============================================")
		fmt.Println("========= Chosen attack : packet drop =========")
		fmt.Println("===============================================")

		if !validatePacketDropAttackConfiguration(interfaceName, srcIp, destIp, srcPort, destPort, networkLatency, attackDuration) {
			printHelp()
			return
		}
		if !continuous {
			fmt.Println()
			fmt.Println("============ Starting attack ============")
			packet_loss.Attack(interfaceName, srcIp, srcPort, destIp, destPort, packetLossPercentage, attackDuration)
			fmt.Println("=========== Attack terminated ===========")
		} else if sleepDuration <= 0 {
			printHelp()
			return
		} else {
			for {
				fmt.Println()
				fmt.Println("============ Starting attack ============")
				err := packet_loss.Attack(interfaceName, srcIp, srcPort, destIp, destPort, packetLossPercentage, attackDuration)
				if err != nil {
					log.Log.Errorf("Packet drop attack terminated with an error: %s", err)
					return
				}
				fmt.Println("=========== Attack terminated ===========")
				fmt.Println()
				fmt.Printf("Sleeping for %v milliseconds before launching new attack", sleepDuration)
				fmt.Println()
				time.Sleep(time.Duration(sleepDuration) * time.Millisecond)
			}
		}
	default:
		log.Log.Errorf("Attack type is invalid: %s", attack)
		printHelp()
	}
}

func printHelp() {
	log.Log.Info("Usage: chaos latency [options]")
	log.Log.Info()
	log.Log.Info("Options:")
	flag.PrintDefaults()
}

func validateLatencyAttackConfiguration(interfaceName string, srcIp string, destIp string, srcPort string, destPort string, networkLatency string, attackDuration string) bool {
	if networkLatency == "" {
		log.Log.Error("network latency cannot be empty")
		return false
	}
	return validateBaseConfiguraion(interfaceName, attackDuration, srcIp, destIp, srcPort, destPort)

}

func validatePacketDropAttackConfiguration(interfaceName string, srcIp string, destIp string, srcPort string, destPort string, packetDropPercentage string, attackDuration string) bool {
	if packetDropPercentage == "" {
		log.Log.Error("packet drop percentage cannt be empty")
		return false
	}
	return validateBaseConfiguraion(interfaceName, attackDuration, srcIp, destIp, srcPort, destPort)
}

func validateBaseConfiguraion(interfaceName string, attackDuration string, srcIp string, destIp string, srcPort string, destPort string) bool {
	if interfaceName == "" {
		log.Log.Error("network interface name cannt be empty")
		return false
	} else if attackDuration == "" {
		log.Log.Error("attack duration cannot be empty")
	} else if destPort == "22" || srcPort == "22" {
		log.Log.Error("dest / src port cannot be 22")
		return false
	} else if srcPort == "" && srcIp == "" && destPort == "" && destIp == "" {
		log.Log.Error("no src and dst endpoints are defined")
		return false
	} else if srcIp == "" && destIp != "" {
		log.Log.Error("src ip cannot be empty")
		return false
	}
	return true
}

func waitForTermination(interfaceFlag string, signals <-chan os.Signal) {
	// Wait for termination signals
	<-signals

	// Perform rules cleanup
	log.Log.Infof("Clearing root queue discipline for interface %s", interfaceFlag)
	cmd := exec.Command("tc", "qdisc", "del", "dev", interfaceFlag, "root")
	output, err := cmd.CombinedOutput()
	if err != nil {
		log.Log.Errorf("Error clearing root qdisc: %s", err)
		log.Log.Infof("Output: %s", string(output))
	}

	// Exit the application gracefully
	os.Exit(0)
}
