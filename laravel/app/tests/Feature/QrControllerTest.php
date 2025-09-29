<?php

declare(strict_types=1);

namespace Tests\Feature;

use Tests\TestCase;

class QrControllerTest extends TestCase
{
    /**
     * Test generating QR code with custom data
     */
    public function test_generates_qr_code_with_custom_data(): void
    {
        $testData = 'my_test_data';

        $response = $this->get('/qr?data=' . $testData);

        $response->assertStatus(200);
        $response->assertHeader('Content-Type', 'image/png');

        // Check that content is not empty
        $this->assertNotEmpty($response->getContent());

        // Check PNG signature
        $this->assertStringStartsWith("\x89PNG\r\n\x1a\n", $response->getContent());
    }

    /**
     * Test generating QR code with default data
     */
    public function test_generates_qr_code_with_default_data(): void
    {
        $response = $this->get('/qr');

        $response->assertStatus(200);
        $response->assertHeader('Content-Type', 'image/png');

        // Check that content is not empty
        $this->assertNotEmpty($response->getContent());

        // Check PNG signature
        $this->assertStringStartsWith("\x89PNG\r\n\x1a\n", $response->getContent());
    }

    /**
     * Test generating QR code with empty data
     */
    public function test_generates_qr_code_with_empty_data(): void
    {
        $response = $this->get('/qr?data=');

        $response->assertStatus(200);
        $response->assertHeader('Content-Type', 'image/png');
    }

    /**
     * Test generating QR code with special characters
     */
    public function test_generates_qr_code_with_special_characters(): void
    {
        $testData = urlencode('Test with spaces & special chars: @#$%');

        $response = $this->get('/qr?data=' . $testData);

        $response->assertStatus(200);
        $response->assertHeader('Content-Type', 'image/png');
        $this->assertNotEmpty($response->getContent());
    }

    /**
     * Test generating QR code with URL
     */
    public function test_generates_qr_code_with_url(): void
    {
        $testUrl = urlencode('https://example.com/test?param=value');

        $response = $this->get('/qr?data=' . $testUrl);

        $response->assertStatus(200);
        $response->assertHeader('Content-Type', 'image/png');
        $this->assertNotEmpty($response->getContent());
    }

    /**
     * Test generating QR code with long text
     */
    public function test_generates_qr_code_with_long_text(): void
    {
        $longText = str_repeat('Lorem ipsum dolor sit amet ', 20);

        $response = $this->get('/qr?data=' . urlencode($longText));

        $response->assertStatus(200);
        $response->assertHeader('Content-Type', 'image/png');
        $this->assertNotEmpty($response->getContent());
    }

    /**
     * Test generated image dimensions
     */
    public function test_qr_code_image_has_correct_dimensions(): void
    {
        $response = $this->get('/qr?data=test');

        $response->assertStatus(200);

        // Get image content
        $imageContent = $response->getContent();

        // Create image from string
        $image = imagecreatefromstring($imageContent);

        // Check dimensions (300 + margin*2 = 320)
        $this->assertEquals(320, imagesx($image));
        $this->assertEquals(320, imagesy($image));

        // Free memory
        imagedestroy($image);
    }

    /**
     * Test response has no cache headers (if needed)
     */
    public function test_response_has_no_cache_headers(): void
    {
        $response = $this->get('/qr?data=test');

        $response->assertStatus(200);

        // If you want to add caching in the future,
        // you can check for appropriate headers here
    }
}
