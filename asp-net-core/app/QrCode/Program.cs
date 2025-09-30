// Copyright (c) DevBox. All rights reserved.

using System.Drawing;
using QRCoder;

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

app.MapGet("/", () => "This is the ASP.NET Core in DevBox.");

app.MapGet("/qr", (string? data) =>
{
    if (string.IsNullOrWhiteSpace(data))
    {
        return Results.BadRequest("Parameter 'data' is required.");
    }

    using var qrGenerator = new QRCodeGenerator();
    var qrCodeData = qrGenerator.CreateQrCode(data, QRCodeGenerator.ECCLevel.Q);

    using var pngGenerator = new PngByteQRCode(qrCodeData);
    var pngBytes = pngGenerator.GetGraphic(
        pixelsPerModule: 20,
        darkColor: Color.Black,
        lightColor: Color.White);

    return Results.File(pngBytes, contentType: "image/png", fileDownloadName: null);
});

await app.RunAsync();

public partial class Program
{
}
