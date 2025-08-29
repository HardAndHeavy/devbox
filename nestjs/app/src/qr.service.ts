import { Injectable } from '@nestjs/common';
import * as QRCode from 'qrcode';

@Injectable()
export class QrService {
  async generateQr(data: string): Promise<Buffer> {
    return QRCode.toBuffer(data, {
      errorCorrectionLevel: 'H',
      type: 'png',
      quality: 0.3,
      margin: 1,
      color: { dark: '#000', light: '#FFF' }
    });
  }
}
