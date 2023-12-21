const express = require('express');
const cors = require('cors');
const morgan = require('morgan');
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = require('./api.json');

const {
  app: { port },
} = require('./src/app/config');

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cors());
app.use(morgan('dev'));

app.get('/', (req, res) => {
  res.redirect('/doc');
});

app.use('/doc', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

const v1 = require('./src/routes/index');

app.use('/api/v1/', v1);

app.use(require('./src/errors/NotFoundHandler'));
app.use(require('./src/errors/ErrorHandler'));

app.listen(port, () => console.log('Server running... ::', port));
