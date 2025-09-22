package main

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"image/png"
	"net/http"
	"net/http/httptest"
	"net/url"
	"strconv"
	"testing"

	"github.com/gin-gonic/gin"            //nolint:depguard
	"github.com/skip2/go-qrcode"          //nolint:depguard
	"github.com/stretchr/testify/assert"  //nolint:depguard
	"github.com/stretchr/testify/require" //nolint:depguard
)

func setupRouter() *gin.Engine {
	gin.SetMode(gin.TestMode)
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

		pngData, err := qrcode.Encode(data, qrcode.Medium, size)
		if err != nil {
			context.JSON(http.StatusInternalServerError, gin.H{"error": fmt.Sprintf("QR generation error: %v", err)})

			return
		}

		context.Data(http.StatusOK, "image/png", pngData)
	})

	return router
}

func TestRootEndpoint(t *testing.T) {
	t.Parallel()

	router := setupRouter()

	t.Run("Should return welcome message on root endpoint", func(t *testing.T) {
		t.Parallel()

		recorder := httptest.NewRecorder()
		req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/", nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusOK, recorder.Code)
		assert.Contains(t, recorder.Body.String(), "This is the Gin framework in DevBox.")
	})
}

func TestQRGenerationMissingData(t *testing.T) {
	t.Parallel()

	router := setupRouter()

	t.Run("Missing data parameter raises error", func(t *testing.T) {
		t.Parallel()

		recorder := httptest.NewRecorder()
		req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr", nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusBadRequest, recorder.Code)

		var response map[string]string

		err := json.Unmarshal(recorder.Body.Bytes(), &response)
		require.NoError(t, err)
		assert.Equal(t, "data parameter is required", response["error"])
	})
}

func TestQRGenerationSuccess(t *testing.T) {
	t.Parallel()

	router := setupRouter()

	t.Run("Successful QR generation with default params", func(t *testing.T) {
		t.Parallel()

		recorder := httptest.NewRecorder()
		req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data=https://example.com", nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusOK, recorder.Code)
		assert.Equal(t, "image/png", recorder.Header().Get("Content-Type"))
		assert.NotEmpty(t, recorder.Body.Bytes())

		assert.True(t, bytes.HasPrefix(recorder.Body.Bytes(), []byte{0x89, 0x50, 0x4E, 0x47}))

		_, err := png.Decode(bytes.NewReader(recorder.Body.Bytes()))
		assert.NoError(t, err)
	})
}

func TestQRGenerationCustomSize(t *testing.T) {
	t.Parallel()

	router := setupRouter()

	t.Run("Successful QR generation with custom size", func(t *testing.T) {
		t.Parallel()

		const (
			minExpectedSize = 250
			maxExpectedSize = 350
		)

		recorder := httptest.NewRecorder()
		req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data=test-data&size=300", nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusOK, recorder.Code)
		assert.Equal(t, "image/png", recorder.Header().Get("Content-Type"))

		img, err := png.Decode(bytes.NewReader(recorder.Body.Bytes()))
		require.NoError(t, err)

		bounds := img.Bounds()
		assert.GreaterOrEqual(t, bounds.Max.X, minExpectedSize)
		assert.LessOrEqual(t, bounds.Max.X, maxExpectedSize)
	})
}

func TestQRGenerationInvalidSize(t *testing.T) {
	t.Parallel()

	router := setupRouter()

	testCases := []struct {
		name           string
		size           string
		expectedStatus int
		expectedError  string
	}{
		{
			name:           "non-numeric size",
			size:           "invalid",
			expectedStatus: http.StatusBadRequest,
			expectedError:  "invalid size parameter",
		},
		{
			name:           "too small size",
			size:           "0",
			expectedStatus: http.StatusBadRequest,
			expectedError:  "invalid size parameter",
		},
		{
			name:           "too large size",
			size:           "1001",
			expectedStatus: http.StatusBadRequest,
			expectedError:  "invalid size parameter",
		},
	}

	for _, testCase := range testCases {
		t.Run(testCase.name, func(t *testing.T) {
			t.Parallel()

			recorder := httptest.NewRecorder()
			req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data=test&size="+testCase.size, nil)
			router.ServeHTTP(recorder, req)

			assert.Equal(t, testCase.expectedStatus, recorder.Code)

			var response map[string]string

			err := json.Unmarshal(recorder.Body.Bytes(), &response)
			require.NoError(t, err)
			assert.Equal(t, testCase.expectedError, response["error"])
		})
	}
}

func TestQRGenerationConsistency(t *testing.T) {
	t.Parallel()

	router := setupRouter()

	t.Run("Identical data generates identical QR", func(t *testing.T) {
		t.Parallel()

		testData := "consistent data"

		recorder1 := httptest.NewRecorder()
		req1, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data="+testData, nil)
		router.ServeHTTP(recorder1, req1)

		recorder2 := httptest.NewRecorder()
		req2, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data="+testData, nil)
		router.ServeHTTP(recorder2, req2)

		assert.Equal(t, http.StatusOK, recorder1.Code)
		assert.Equal(t, http.StatusOK, recorder2.Code)
		assert.Equal(t, recorder1.Body.Bytes(), recorder2.Body.Bytes())
	})

	t.Run("Different data generates different QR", func(t *testing.T) {
		t.Parallel()

		recorder1 := httptest.NewRecorder()
		req1, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data=data1", nil)
		router.ServeHTTP(recorder1, req1)

		recorder2 := httptest.NewRecorder()
		req2, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data=data2", nil)
		router.ServeHTTP(recorder2, req2)

		assert.Equal(t, http.StatusOK, recorder1.Code)
		assert.Equal(t, http.StatusOK, recorder2.Code)
		assert.NotEqual(t, recorder1.Body.Bytes(), recorder2.Body.Bytes())
	})
}

func TestQRGenerationSpecialCharacters(t *testing.T) {
	t.Parallel()

	router := setupRouter()

	t.Run("QR generation with special characters", func(t *testing.T) {
		t.Parallel()

		specialData := "Test with special chars: !@#$%^&*()_+={[}]|\\:;\"'<,>.?/"
		encodedData := url.QueryEscape(specialData)

		recorder := httptest.NewRecorder()
		req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data="+encodedData, nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusOK, recorder.Code)
		assert.NotEmpty(t, recorder.Body.Bytes())

		_, err := png.Decode(bytes.NewReader(recorder.Body.Bytes()))
		assert.NoError(t, err)
	})

	t.Run("QR generation with unicode", func(t *testing.T) {
		t.Parallel()

		unicodeData := "Test with unicode: Hello World 🎉"
		encodedData := url.QueryEscape(unicodeData)

		recorder := httptest.NewRecorder()
		req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data="+encodedData, nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusOK, recorder.Code)
		assert.NotEmpty(t, recorder.Body.Bytes())

		_, err := png.Decode(bytes.NewReader(recorder.Body.Bytes()))
		assert.NoError(t, err)
	})
}

func TestQRGenerationEdgeCases(t *testing.T) {
	t.Parallel()

	router := setupRouter()

	t.Run("QR generation with empty string after trimming", func(t *testing.T) {
		t.Parallel()

		recorder := httptest.NewRecorder()
		req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data=", nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusBadRequest, recorder.Code)

		var response map[string]string

		err := json.Unmarshal(recorder.Body.Bytes(), &response)
		require.NoError(t, err)
		assert.Equal(t, "data parameter is required", response["error"])
	})

	t.Run("QR generation with very long data", func(t *testing.T) {
		t.Parallel()

		const dataLength = 1000

		longData := ""
		for range dataLength {
			longData += "a"
		}

		recorder := httptest.NewRecorder()
		req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data="+longData, nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusOK, recorder.Code)
		assert.NotEmpty(t, recorder.Body.Bytes())
	})
}

//nolint:funlen
func TestQRSizeVariations(t *testing.T) {
	t.Parallel()

	router := setupRouter()

	const (
		minValidSize   = 900
		maxValidSize   = 1100
		defaultMinSize = 150
		defaultMaxSize = 250
	)

	testCases := []struct {
		name           string
		size           string
		expectedStatus int
		checkSize      bool
		minSize        int
		maxSize        int
	}{
		{
			name:           "Minimum valid size",
			size:           "1",
			expectedStatus: http.StatusOK,
			checkSize:      false,
			minSize:        0,
			maxSize:        0,
		},
		{
			name:           "Maximum valid size",
			size:           "1000",
			expectedStatus: http.StatusOK,
			checkSize:      true,
			minSize:        minValidSize,
			maxSize:        maxValidSize,
		},
		{
			name:           "Default size when not specified",
			size:           "",
			expectedStatus: http.StatusOK,
			checkSize:      true,
			minSize:        defaultMinSize,
			maxSize:        defaultMaxSize,
		},
		{
			name:           "Negative size",
			size:           "-10",
			expectedStatus: http.StatusBadRequest,
			checkSize:      false,
			minSize:        0,
			maxSize:        0,
		},
		{
			name:           "Zero size",
			size:           "0",
			expectedStatus: http.StatusBadRequest,
			checkSize:      false,
			minSize:        0,
			maxSize:        0,
		},
	}

	for _, testCase := range testCases {
		t.Run(testCase.name, func(t *testing.T) {
			t.Parallel()

			recorder := httptest.NewRecorder()

			url := "/qr?data=test"
			if testCase.size != "" {
				url += "&size=" + testCase.size
			}

			req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, url, nil)
			router.ServeHTTP(recorder, req)

			assert.Equal(t, testCase.expectedStatus, recorder.Code)

			if testCase.expectedStatus == http.StatusOK && testCase.checkSize {
				img, err := png.Decode(bytes.NewReader(recorder.Body.Bytes()))
				require.NoError(t, err)

				bounds := img.Bounds()
				assert.GreaterOrEqual(t, bounds.Max.X, testCase.minSize)
				assert.LessOrEqual(t, bounds.Max.X, testCase.maxSize)
			}
		})
	}
}

func BenchmarkQRGeneration(b *testing.B) {
	router := setupRouter()

	b.Run("Small data", func(b *testing.B) {
		for range b.N {
			recorder := httptest.NewRecorder()
			req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data=test", nil)
			router.ServeHTTP(recorder, req)
		}
	})

	b.Run("Medium data", func(b *testing.B) {
		data := "https://example.com/some/path/to/resource?param1=value1&param2=value2"

		for range b.N {
			recorder := httptest.NewRecorder()
			req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data="+data, nil)
			router.ServeHTTP(recorder, req)
		}
	})

	b.Run("Large data", func(b *testing.B) {
		const dataLength = 500

		data := ""
		for range dataLength {
			data += "a"
		}

		for range b.N {
			recorder := httptest.NewRecorder()
			req, _ := http.NewRequestWithContext(context.Background(), http.MethodGet, "/qr?data="+data, nil)
			router.ServeHTTP(recorder, req)
		}
	})
}
