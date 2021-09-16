const { Router } = require('express');
const colonyController = require('../controllers/colonies');
const authentication = require('../middleware/auth');

const router = Router();

router.post('/', authentication, colonyController.createColony);

router.post('/delete', authentication, colonyController.deleteColony);

router.post('/share', authentication, colonyController.shareColony);

module.exports = router;
