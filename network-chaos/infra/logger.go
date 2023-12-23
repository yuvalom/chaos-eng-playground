package infra

import (
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	"os"
)

// Log is global single instance
var Log *zap.SugaredLogger

const (
	JsonEncoding    = "json"
	ConsoleEncoding = "console"
	TimeEncoding    = "timestamp"
)

// init called when the class is initiated
func init() {
	level := zap.InfoLevel
	var encoding = JsonEncoding
	if _, DEBUG := os.LookupEnv("LOG_LEVEL_DEBUG"); DEBUG {
		level = zap.DebugLevel
		encoding = ConsoleEncoding
	}
	config := zap.NewProductionConfig()
	config.Level = zap.NewAtomicLevelAt(level)
	config.Encoding = encoding
	config.EncoderConfig.EncodeTime = zapcore.ISO8601TimeEncoder
	config.EncoderConfig.TimeKey = TimeEncoding
	loggerBuilder, _ := config.Build()
	Log = loggerBuilder.Sugar()
}

func DeferLogger() {
	err := Log.Sync()
	if err != nil {
		return
	}
}
