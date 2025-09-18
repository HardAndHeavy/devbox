import { Test, TestingModule } from '@nestjs/testing';
import { QrService } from './qr.service';

describe('QrService', () => {
  let service: QrService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [QrService],
    }).compile();

    service = module.get<QrService>(QrService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('generateQr', () => {
    it('should generate Buffer with QR code', async () => {
      const testData = 'test data';
      const result = await service.generateQr(testData);

      expect(result).toBeInstanceOf(Buffer);
      expect(result.length).toBeGreaterThan(0);
    });

    it('should generate identical QR codes for identical data', async () => {
      const testData = 'consistent data';

      const qr1 = await service.generateQr(testData);
      const qr2 = await service.generateQr(testData);

      expect(qr1.equals(qr2)).toBe(true);
    });

    it('should generate different QR codes for different data', async () => {
      const qr1 = await service.generateQr('data1');
      const qr2 = await service.generateQr('data2');

      expect(qr1.equals(qr2)).toBe(false);
    });
  });
});
