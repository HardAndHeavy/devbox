// Package main implements a web server with QR code generation functionality.
package main

import (
	"fmt"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin" //nolint:depguard
	"github.com/skip2/go-qrcode" //nolint:depguard
)

// main is the entry point of the application.
func main() {
	router := gin.Default()

	router.GET("/", func(context *gin.Context) {
		context.String(http.StatusOK, "This is the Gin framework in DevBox.")
	})

	router.GET("/qr", func(context *gin.Context) {
		data := context.Query("data")
		if data == "" {
			context.JSON(http.StatusBadRequest, gin.H{"error": "data parameter is required"})

			return
		}

		sizeStr := context.DefaultQuery("size", "200")

		size, err := strconv.Atoi(sizeStr)
		if err != nil || size < 1 || size > 1000 {
			context.JSON(http.StatusBadRequest, gin.H{"error": "invalid size parameter"})

			return
		}

		png, err := qrcode.Encode(data, qrcode.Medium, size)
		if err != nil {
			context.JSON(http.StatusInternalServerError, gin.H{"error": fmt.Sprintf("QR generation error: %v", err)})

			return
		}

		context.Data(http.StatusOK, "image/png", png)
	})

	err := router.Run()
	if err != nil {
		panic(fmt.Sprintf("Failed to start server: %v", err))
	}
}
