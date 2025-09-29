<?php

declare(strict_types=1);

namespace Tests\Unit;

use App\Http\Controllers\QrController;
use Illuminate\Http\Request;
use Tests\TestCase;

class QrControllerUnitTest extends TestCase
{
    /**
     * Test controller can be instantiated
     */
    public function test_controller_can_be_instantiated(): void
    {
        $controller = new QrController();
        $this->assertInstanceOf(QrController::class, $controller);
    }

    /**
     * Test generate method returns response
     */
    public function test_generate_method_returns_response(): void
    {
        $controller = new QrController();

        $request = Request::create('/qr', 'GET', ['data' => 'test_data']);

        $response = $controller->generate($request);

        $this->assertInstanceOf(\Illuminate\Http\Response::class, $response);
        $this->assertEquals(200, $response->getStatusCode());
        $this->assertEquals('image/png', $response->headers->get('Content-Type'));
    }

    /**
     * Test generate with no data parameter
     */
    public function test_generate_with_no_data_parameter(): void
    {
        $controller = new QrController();

        $request = Request::create('/qr', 'GET', []);

        $response = $controller->generate($request);

        $this->assertInstanceOf(\Illuminate\Http\Response::class, $response);
        $this->assertEquals(200, $response->getStatusCode());
    }
}
