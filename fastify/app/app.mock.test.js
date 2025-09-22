import { jest } from '@jest/globals';

const mockToBuffer = jest.fn();

jest.unstable_mockModule('qrcode', () => ({
  default: {
    toBuffer: mockToBuffer,
  },
}));

const { buildApp } = await import('./app.js');

describe('Fastify QR Code App - Error Handling', () => {
  let app;

  beforeEach(() => {
    app = buildApp({ logger: false });
    jest.clearAllMocks();
  });

  afterEach(async () => {
    await app.close();
  });

  it('should return 500 error when QR code generation fails', async () => {
    const testData = 'test-data';

    mockToBuffer.mockRejectedValue(new Error('Generation failed'));

    const response = await app.inject({
      method: 'GET',
      url: `/qr?data=${encodeURIComponent(testData)}`,
    });

    expect(response.statusCode).toBe(500);
    expect(JSON.parse(response.body)).toEqual({
      error: 'Error generating QR code',
    });
    expect(mockToBuffer).toHaveBeenCalledWith(testData, expect.any(Object));
  });
});
