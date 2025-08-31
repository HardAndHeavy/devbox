package main

import (
	"fmt"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/skip2/go-qrcode"
)

func main() {
	r := gin.Default()

	r.GET("/", func(c *gin.Context) {
		c.String(http.StatusOK, "This is the Gin framework in DevBox.")
	})

	r.GET("/qr", func(c *gin.Context) {
		data := c.Query("data")
		if data == "" {
			c.JSON(http.StatusBadRequest, gin.H{"error": "data parameter is required"})
			return
		}

		sizeStr := c.DefaultQuery("size", "200")
		size, err := strconv.Atoi(sizeStr)
		if err != nil || size < 1 || size > 1000 {
			c.JSON(http.StatusBadRequest, gin.H{"error": "invalid size parameter"})
			return
		}

		png, err := qrcode.Encode(data, qrcode.Medium, size)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": fmt.Sprintf("QR generation error: %v", err)})
			return
		}

		c.Data(http.StatusOK, "image/png", png)
	})

	r.Run()
}
