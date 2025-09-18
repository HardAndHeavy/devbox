import { Test, TestingModule } from '@nestjs/testing';
import { QrController } from './qr.controller';
import { QrService } from './qr.service';
import { Response } from 'express';
import QRCode from 'qrcode';

describe('QrController', () => {
  let qrController: QrController;
  let qrService: QrService;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [QrController],
      providers: [QrService],
    }).compile();

    qrController = app.get<QrController>(QrController);
    qrService = app.get<QrService>(QrService);
  });

  describe('getQr', () => {
    let mockResponse: Partial<Response>;

    beforeEach(() => {
      mockResponse = {
        setHeader: jest.fn(),
        send: jest.fn(),
        status: jest.fn().mockReturnThis(),
      };
    });

    it('should return 400 error if data parameter is missing', async () => {
      await qrController.getQr(undefined, mockResponse as Response);

      expect(mockResponse.status).toHaveBeenCalledWith(400);
      expect(mockResponse.send).toHaveBeenCalledWith(
        'Missing "data" query parameter',
      );
    });

    it('should successfully generate QR code', async () => {
      const testData = 'https://example.com';
      const mockBuffer = Buffer.from('mock-qr-image');

      const generateQrSpy = jest
        .spyOn(qrService, 'generateQr')
        .mockResolvedValue(mockBuffer);

      await qrController.getQr(testData, mockResponse as Response);

      expect(generateQrSpy).toHaveBeenCalledWith(testData);
      expect(mockResponse.setHeader).toHaveBeenCalledWith(
        'Content-Type',
        'image/png',
      );
      expect(mockResponse.setHeader).toHaveBeenCalledWith(
        'Content-Disposition',
        'inline; filename="qrcode.png"',
      );
      expect(mockResponse.send).toHaveBeenCalledWith(mockBuffer);
    });

    it('should return 500 error when QR code generation fails', async () => {
      const testData = 'test-data';

      jest
        .spyOn(qrService, 'generateQr')
        .mockRejectedValue(new Error('Generation failed'));

      await qrController.getQr(testData, mockResponse as Response);

      expect(mockResponse.status).toHaveBeenCalledWith(500);
      expect(mockResponse.send).toHaveBeenCalledWith(
        'Error generating QR code',
      );
    });

    it('should generate correct QR code (integration test)', async () => {
      const testData = 'Test QR Data';

      const generatedQr = await qrService.generateQr(testData);

      const expectedQr = await QRCode.toBuffer(testData, {
        errorCorrectionLevel: 'H',
        margin: 1,
        color: {
          dark: '#000000',
          light: '#FFFFFF',
        },
      });

      expect(generatedQr.equals(expectedQr)).toBe(true);
    });
  });
});
