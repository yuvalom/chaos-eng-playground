package packet_loss

import (
	log "com/securithings/network-chaos/infra"
	"errors"
	"os/exec"
	"strconv"
	"time"
)

func Attack(interfaceName string, srcIpAddress string, srcPort string, dstIpAddress string, dstPort string, lossPercentage string, attackDuration string) error {
	duration, err := strconv.ParseInt(attackDuration, 10, 64)
	if err != nil {
		log.Log.Error("Error:", err)
		return errors.New("failed to parse attack duration")
	}
	// Clearing all root qdisc
	_ = clearRootQDisc(interfaceName)
	// Add root qdisc
	err = addRootQDisc(interfaceName)
	if err != nil {
		return err
	}
	// Add child qdisc with packet loss
	err = configureNetworkPacketLossRule(interfaceName, lossPercentage)
	if err != nil {
		return err
	}
	// Add filters
	err = addFilter(interfaceName, srcIpAddress, srcPort, dstIpAddress, dstPort)
	if err != nil {
		return err
	}
	log.Log.Info("Network packet loss rules added successfully.")
	// Sleep for given duration
	log.Log.Infof("Sleeping for %s milliseconds.\n", attackDuration)

	time.Sleep(time.Duration(duration) * time.Millisecond)
	// Terminate attack
	log.Log.Info("Terminating packet loss attack")
	// Clearing all root qdisc
	err = clearRootQDisc(interfaceName)
	if err != nil {
		return err
	}
	log.Log.Info("Successfull terminated packet loss attack")
	return nil
}

func clearRootQDisc(interfaceName string) error {
	// Clearing all root qdisc
	log.Log.Infof("Clearing root queue discipline for interface %s", interfaceName)
	cmd := exec.Command("tc", "qdisc", "del", "dev", interfaceName, "root")
	output, err := cmd.CombinedOutput()
	if err != nil {
		log.Log.Errorf("Error clearing root qdisc: %s", err)
		log.Log.Infof("Output: %s", string(output))
		return errors.New("error clearing root qdisc")
	}
	return nil
}

func addRootQDisc(interfaceName string) error {
	log.Log.Infof("Adding root queue discipline for interface %s", interfaceName)
	cmd := exec.Command("tc", "qdisc", "add", "dev", interfaceName, "root", "handle", "1:", "prio", "priomap", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0")
	output, err := cmd.CombinedOutput()
	if err != nil {
		log.Log.Errorf("Error adding root qdisc: %s", err)
		log.Log.Infof("Output: %s", string(output))
		return errors.New("error adding root qdisc")
	}
	return nil
}

func configureNetworkPacketLossRule(interfaceName string, lossPercentage string) error {
	// Add child qdisc with packet loss
	log.Log.Infof("Adding child queue discipline for interface %s with configured %s%% packet loss ", interfaceName, lossPercentage)
	cmd := exec.Command("tc", "qdisc", "add", "dev", interfaceName, "parent", "1:2", "handle", "10:", "netem", "loss", lossPercentage+"%")
	if err := cmd.Run(); err != nil {
		log.Log.Errorf("Error adding child qdisc: %s. Rolling back changes", err)
		cmd = exec.Command("tc", "qdisc", "del", "dev", interfaceName, "root")
		if err := cmd.Run(); err != nil {
			log.Log.Errorf("Error deleting root qdisc: %s", err)
		}
		return errors.New("error configuring network packet loss")
	}
	return nil
}

func addFilter(interfaceName string, srcIpAddress string, srcPort string, dstIpAddress string, dstPort string) error {
	if dstIpAddress != "" && srcIpAddress != "" {
		// Create filter that will isolate and protect SSH sessions from being affected by the network emulation
		log.Log.Infof("Adding filter for SSH session isolation on source %s ", srcIpAddress)
		cmd := exec.Command("tc", "filter", "add", "dev", interfaceName, "parent", "1:0", "prio", "1", "protocol", "ip", "u32", "match", "ip", "src", srcIpAddress, "match", "ip", "sport", "22", "0xFFFF", "flowid", "1:0")
		if err := cmd.Run(); err != nil {
			log.Log.Errorf("Error adding filter for SSH sessions: %s", err)
			clearRootQDisc(interfaceName)
			return errors.New("error adding filter for SSH sessions")
		}
		// Add filter to match traffic to specific destination host
		log.Log.Infof("Adding filter for interface %s with parent qdisc that match source ip %s and destination ip %s", interfaceName, srcIpAddress, dstIpAddress)
		cmd = exec.Command("tc", "filter", "add", "dev", interfaceName, "parent", "1:0", "prio", "2", "protocol", "ip", "u32", "match", "ip", "src", srcIpAddress, "match", "ip", "dst", dstIpAddress, "flowid", "1:2")
		if err := cmd.Run(); err != nil {
			log.Log.Errorf("Error adding filter for source and destination IP addresses: %s", err)
			clearRootQDisc(interfaceName)
			return errors.New("error adding filter for source and destination IP addresses")
		}
	} else if srcIpAddress != "" {
		// Create filter that will isolate and protect SSH sessions from being affected by the network emulation
		log.Log.Infof("Adding filter for SSH session isolation on source %s ", srcIpAddress)
		cmd := exec.Command("tc", "filter", "add", "dev", interfaceName, "parent", "1:0", "prio", "1", "protocol", "ip", "u32", "match", "ip", "src", srcIpAddress, "match", "ip", "sport", "22", "0xFFFF", "flowid", "1:0")
		if err := cmd.Run(); err != nil {
			log.Log.Errorf("Error adding filter for SSH sessions: %s", err)
			clearRootQDisc(interfaceName)
			return errors.New("error adding filter for SSH session")
		}

		// Add filter to match traffic to specific destination host
		log.Log.Infof("Adding filter for interface %s with parent qdisc that match source ip %s ", interfaceName, srcIpAddress)
		cmd = exec.Command("tc", "filter", "add", "dev", interfaceName, "parent", "1:0", "prio", "2", "protocol", "ip", "u32", "match", "ip", "dst", srcIpAddress, "flowid", "1:2")
		if err := cmd.Run(); err != nil {
			log.Log.Errorf("Error adding filter for source IP address: %s", err)
			clearRootQDisc(interfaceName)
			return errors.New("error adding filter for source IP address")
		}
	}

	if srcPort != "" {
		// Add filter to match traffic to specific port
		log.Log.Infof("Adding filter for interface %s with parent qdisc that match source port %s ", interfaceName, srcPort)
		cmd := exec.Command("tc", "filter", "add", "dev", interfaceName, "parent", "1:0", "protocol", "ip", "u32", "match", "ip", "sport", srcPort, "0xffff", "flowid", "1:2")
		if err := cmd.Run(); err != nil {
			log.Log.Errorf("Error adding filter on srouce port: %s. Rolling back changes", err)
			clearRootQDisc(interfaceName)
			return errors.New("error adding filter for source port")
		}
	}

	if dstPort != "" {
		// Add filter to match traffic to specific port
		log.Log.Infof("Adding filter for interface %s with parent qdisc that match destination port %s ", interfaceName, dstPort)
		cmd := exec.Command("tc", "filter", "add", "dev", interfaceName, "parent", "1:0", "protocol", "ip", "u32", "match", "ip", "dport", dstPort, "0xffff", "flowid", "1:2")
		if err := cmd.Run(); err != nil {
			log.Log.Errorf("Error adding filter on destination port: %s. Rolling back changes", err)
			clearRootQDisc(interfaceName)
			return errors.New("error adding filter for destination port")
		}
	}
	return nil

}
