import { Injectable } from '@nestjs/common';
import QRCode from 'qrcode';

@Injectable()
export class QrService {
  async generateQr(data: string): Promise<Buffer> {
    return await QRCode.toBuffer(data, {
      errorCorrectionLevel: 'H',
      margin: 1,
      color: {
        dark: '#000000',
        light: '#FFFFFF',
      },
    });
  }
}
