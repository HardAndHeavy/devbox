import Fastify from 'fastify';
import QRCode from 'qrcode';

export const buildApp = (options = {}) => {
  const app = Fastify({ logger: true, ...options });

  app.get('/', () => 'This is the Fastify framework in DevBox.');

  app.get('/qr', async (request, reply) => {
    const { data } = request.query;

    if (!data) {
      return reply
        .status(400)
        .send({ error: 'Missing "data" query parameter' });
    }

    try {
      const qrBuffer = await QRCode.toBuffer(data, {
        errorCorrectionLevel: 'H',
        type: 'png',
        quality: 0.3,
        margin: 1,
        color: { dark: '#000', light: '#FFF' },
      });

      reply
        .header('Content-Type', 'image/png')
        .header('Content-Disposition', 'inline; filename="qrcode.png"')
        .send(qrBuffer);
    } catch (error) {
      app.log.error(error);
      reply.status(500).send({ error: 'Error generating QR code' });
    }
  });

  return app;
};
