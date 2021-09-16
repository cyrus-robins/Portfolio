const jwt = require('jsonwebtoken');

const NOT_SO_SECRET_KEY = 'thisShouldBeAPrivateKeyYouInjectWhenYouDeploy';

const verifyToken = (token) => jwt.verify(token, NOT_SO_SECRET_KEY);

const createToken = (data) => jwt.sign(data, NOT_SO_SECRET_KEY);

module.exports = { createToken, verifyToken };
