import Fastify from 'fastify';
import QRCode from 'qrcode';

const main = async () => {
  const app = Fastify({ logger: true });

  app.get('/', () => 'This is the Fastify framework in DevBox.');

  app.get('/qr', async (request, reply) => {
    const { data } = request.query;

    if (!data) {
      return reply.status(400).send('Missing "data" query parameter');
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
      reply.status(500).send('Error generating QR code');
    }
  });

  await app.listen({ port: 3000, host: '0.0.0.0' }, (err) => {
    if (err) {
      app.log.error(err);
      process.exit(1);
    }
  });
};

await main();
