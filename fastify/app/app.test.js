import QRCode from 'qrcode';
import { buildApp } from './app.js';

describe('Fastify QR Code App', () => {
  let app;

  beforeEach(() => {
    app = buildApp({ logger: false });
  });

  afterEach(async () => {
    await app.close();
  });

  describe('GET /', () => {
    it('should return welcome message', async () => {
      const response = await app.inject({
        method: 'GET',
        url: '/',
      });

      expect(response.statusCode).toBe(200);
      expect(response.body).toBe('This is the Fastify framework in DevBox.');
    });
  });

  describe('GET /qr', () => {
    it('should return 400 error if data parameter is missing', async () => {
      const response = await app.inject({
        method: 'GET',
        url: '/qr',
      });

      expect(response.statusCode).toBe(400);
      expect(JSON.parse(response.body)).toEqual({
        error: 'Missing "data" query parameter',
      });
    });

    it('should successfully generate QR code', async () => {
      const testData = 'https://example.com';

      const response = await app.inject({
        method: 'GET',
        url: `/qr?data=${encodeURIComponent(testData)}`,
      });

      expect(response.statusCode).toBe(200);
      expect(response.headers['content-type']).toBe('image/png');
      expect(response.headers['content-disposition']).toBe(
        'inline; filename="qrcode.png"'
      );
      expect(response.rawPayload).toBeInstanceOf(Buffer);
      expect(response.rawPayload.length).toBeGreaterThan(0);
    });

    it('should generate identical QR codes for identical data', async () => {
      const testData = 'consistent data';

      const response1 = await app.inject({
        method: 'GET',
        url: `/qr?data=${encodeURIComponent(testData)}`,
      });

      const response2 = await app.inject({
        method: 'GET',
        url: `/qr?data=${encodeURIComponent(testData)}`,
      });

      expect(response1.rawPayload.equals(response2.rawPayload)).toBe(true);
    });

    it('should generate different QR codes for different data', async () => {
      const response1 = await app.inject({
        method: 'GET',
        url: `/qr?data=${encodeURIComponent('data1')}`,
      });

      const response2 = await app.inject({
        method: 'GET',
        url: `/qr?data=${encodeURIComponent('data2')}`,
      });

      expect(response1.rawPayload.equals(response2.rawPayload)).toBe(false);
    });

    it('should generate correct QR code (integration test)', async () => {
      const testData = 'Test QR Data';

      const response = await app.inject({
        method: 'GET',
        url: `/qr?data=${encodeURIComponent(testData)}`,
      });

      const expectedQr = await QRCode.toBuffer(testData, {
        errorCorrectionLevel: 'H',
        type: 'png',
        quality: 0.3,
        margin: 1,
        color: { dark: '#000', light: '#FFF' },
      });

      expect(response.rawPayload.equals(expectedQr)).toBe(true);
    });

    it('should handle special characters in data', async () => {
      const testData = 'Special chars: !@#$%^&*()';

      const response = await app.inject({
        method: 'GET',
        url: `/qr?data=${encodeURIComponent(testData)}`,
      });

      expect(response.statusCode).toBe(200);
      expect(response.headers['content-type']).toBe('image/png');
    });

    it('should handle very long data', async () => {
      const testData = 'a'.repeat(1000);

      const response = await app.inject({
        method: 'GET',
        url: `/qr?data=${encodeURIComponent(testData)}`,
      });

      expect(response.statusCode).toBe(200);
      expect(response.headers['content-type']).toBe('image/png');
    });
  });
});
