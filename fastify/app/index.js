import { buildApp } from './app.js';

const main = async () => {
  const app = buildApp();

  await app.listen({ port: 3000, host: '0.0.0.0' }, (err) => {
    if (err) {
      app.log.error(err);
      process.exit(1);
    }
  });
};

await main();
