// Copyright (c) DevBox. All rights reserved.

namespace QrCode.Tests;

using System.Net;
using System.Net.Http.Headers;
using System.Text;
using Microsoft.AspNetCore.Mvc.Testing;
using Xunit;

public class QrCodeEndpointTests : IClassFixture<WebApplicationFactory<Program>>
{
    private readonly WebApplicationFactory<Program> factory;

    public QrCodeEndpointTests(WebApplicationFactory<Program> factory)
    {
        this.factory = factory;
    }

    [Fact]
    public async Task Get_Qr_WithoutData_ReturnsBadRequest()
    {
        var client = this.factory.CreateClient();
        var response = await client.GetAsync("/qr");
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        var content = await response.Content.ReadAsStringAsync();
        Assert.Contains("Parameter 'data' is required", content);
    }

    [Fact]
    public async Task Get_Qr_WithData_ReturnsPngImage()
    {
        var client = this.factory.CreateClient();
        var response = await client.GetAsync("/qr?data=HelloWorld");
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        Assert.Equal("image/png", response.Content.Headers.ContentType?.MediaType);
        var pngBytes = await response.Content.ReadAsByteArrayAsync();
        Assert.True(pngBytes.Length > 0);
        var pngSignature = new byte[] { 137, 80, 78, 71, 13, 10, 26, 10 };
        for (int i = 0; i < Math.Min(pngSignature.Length, pngBytes.Length); i++)
        {
            Assert.Equal(pngSignature[i], pngBytes[i]);
        }
    }

    [Fact]
    public async Task Get_Qr_WithEmptyData_ReturnsBadRequest()
    {
        var client = this.factory.CreateClient();
        var response = await client.GetAsync("/qr?data=");
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        var content = await response.Content.ReadAsStringAsync();
        Assert.Contains("Parameter 'data' is required", content);
    }

    [Fact]
    public async Task Get_Qr_WithWhitespaceData_ReturnsBadRequest()
    {
        var client = this.factory.CreateClient();
        var response = await client.GetAsync("/qr?data=   ");
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        var content = await response.Content.ReadAsStringAsync();
        Assert.Contains("Parameter 'data' is required", content);
    }
}
