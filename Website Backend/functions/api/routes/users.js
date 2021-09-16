const { Router } = require('express');
const userController = require('../controllers/users');
const authentication = require('../middleware/auth');

const router = Router();

router.get('/', authentication, userController.currentUser);
router.post('/', userController.createUser);

module.exports = router;
