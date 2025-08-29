import { Injectable } from '@nestjs/common';

@Injectable()
export class AppService {
  getApp(): string {
    return 'This is the NestJS in DevBox.';
  }
}
