const debug = require('debug')('app:middleware:errors');

/**
 * Simple example of a catch-all error logger -- this is where you'd do
 * some meaningful actions with caught errors like sending them to
 * NewRelic, Rollbar, or DataDog.  But for local development this is a great
 * way to toggle on/off error logging!
 *
 * @param err
 * @param req
 * @param res
 * @param next
 */
const errorLogger = (err, req, res, next) => {
  if (err) {
    debug(err);
    /* Generally exposing the entire error message is a bad idea...
     * usually you only want to expose a user-facing human readable error
     * and/or some JSON the front-end can parse, then send things like the stack
     * trace and original request to a logging service.
     */
    res.status(500).send(err.toString());
    // ... normally would do something meaningful here with it
  }
  next();
};

module.exports = errorLogger;
