import { Controller, Get, Query, Res } from '@nestjs/common';
import type { Response } from 'express';
import { QrService } from './qr.service';

@Controller('qr')
export class QrController {
  constructor(private qrService: QrService) {}

  @Get()
  async getQr(@Query('data') data: string, @Res() res: Response) {
    if (!data) {
      res.status(400).send('Missing "data" query parameter');
      return;
    }
    try {
      const qr = await this.qrService.generateQr(data);
      res.setHeader('Content-Type', 'image/png');
      res.setHeader('Content-Disposition', 'inline; filename="qrcode.png"');
      res.send(qr);
    } catch (error) {
      res.status(500).send('Error generating QR code');
    }
  }
}
