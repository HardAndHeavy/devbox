<?php

declare(strict_types=1);

use App\Http\Controllers\QrController;
use Illuminate\Support\Facades\Route;

Route::get('/', function () {
    return 'This is the Laravel in DevBox.';
});

Route::get('/qr', [QrController::class, 'generate']);
