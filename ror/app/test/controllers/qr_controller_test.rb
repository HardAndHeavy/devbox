# frozen_string_literal: true

require "test_helper"
require "rqrcode"

class QrControllerTest < ActionDispatch::IntegrationTest
  test "should generate QR code with default data" do
    get "/qr"
    assert_response :success
    assert_equal "image/png", @response.content_type
    assert_match(/inline/, @response.headers["Content-Disposition"])
  end

  test "should generate QR code with custom data" do
    get "/qr", params: { data: "https://example.com" }
    assert_response :success
    assert_equal "image/png", @response.content_type
  end

  test "should return valid PNG data" do
    get "/qr", params: { data: "test" }
    assert_response :success
    png_signature = "\x89PNG\r\n\x1A\n".dup.force_encoding("ASCII-8BIT")
    assert_equal png_signature, @response.body[0..7].force_encoding("ASCII-8BIT")
  end

  test "should handle empty string data" do
    get "/qr", params: { data: "" }
    assert_response :success
  end

  test "should handle special characters" do
    get "/qr", params: { data: "Text with special chars: !@#$%^&*()" }
    assert_response :success
    assert_equal "image/png", @response.content_type
  end

  test "should handle Unicode characters" do
    get "/qr", params: { data: "Текст на русском языке 你好" }
    assert_response :success
  end

  test "should generate different QR codes for different data" do
    get "/qr", params: { data: "First QR" }
    first_response = @response.body

    get "/qr", params: { data: "Second QR" }
    second_response = @response.body

    assert_not_equal first_response, second_response
  end

  test "should generate same QR code for same data" do
    get "/qr", params: { data: "Same Data" }
    first_response = @response.body

    get "/qr", params: { data: "Same Data" }
    second_response = @response.body

    assert_equal first_response, second_response
  end

  test "should handle URL data" do
    get "/qr", params: { data: "https://www.example.com/path?param=value" }
    assert_response :success
    assert_not_empty @response.body
  end

  test "should handle reasonably large data" do
    large_data = "a" * 500
    get "/qr", params: { data: large_data }
    assert_response :success
  end

  test "response body should not be empty" do
    get "/qr"
    assert_not_empty @response.body
  end

  test "should handle plain text message" do
    get "/qr", params: { data: "Hello, World!" }
    assert_response :success
    assert_equal "image/png", @response.content_type
  end
end
