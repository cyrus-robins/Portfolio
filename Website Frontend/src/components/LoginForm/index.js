import React, { useState } from 'react';
import { Redirect } from 'react-router-dom';
import { useProfileProvider } from 'contexts/profile';
import { Button, TextField, Link, Container, CssBaseline, Typography } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import GoogleButton from 'react-google-button';
//don't get me started on this
import { auth as auth_ } from 'firebase';
import { auth } from 'components/FirebaseConfig';

const { addToList } = require('components/Tags/index');

const Login = () => {
  const { login, getAllTags, state: { listOfTags } } = useProfileProvider();
  const { register } = useProfileProvider();

  const [userDetails, setUserDetails] = useState({});
  const [redirectToRegister, setRedirectToRegister] = useState(false);
  // eslint-disable-next-line
  const [redirectToDashboard, setRedirectToDashboard] = useState(false);

  const checkAllTags = async () => {
    if(listOfTags === undefined){
      await importTags().then(function() {
      }).catch(function(error) {
        console.error(error);
      });
    }
  }

  

  /** Material-UI */
  const useStyles = makeStyles(theme => (
    // eslint-disable-next-line
    {
    paper: {
      marginTop: theme.spacing(8),
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
    },
    avatar: {
      margin: theme.spacing(1),
      backgroundColor: theme.palette.secondary.main,
    },
    form: {
      width: '100%', // Fix IE 11 issue.
      marginTop: theme.spacing(1),
    },
    submit: {
      margin: theme.spacing(3, 0, 2),
    },
  }));

  const importTags = async () => {
    if(listOfTags === undefined){
      await getAllTags().then(function() {
      }).catch(function(error) {
        console.error(error);
      });
    }
  }

  //call checkall tags
  checkAllTags();


  const classes = useStyles();

  const attemptLogin = (event) => {
    event.preventDefault();

    addToList(listOfTags);

    const { email, password } = userDetails;
    auth.signInWithEmailAndPassword(email, password).catch(function(error) {
      // Handle Errors here.
      console.error(error);
    });

    auth.onAuthStateChanged(function(user) {
      if (user) {
        user.getIdToken(/* forceRefresh */ true).then(function(idToken) {
          login({idToken: idToken});
        }).catch(function(error) {
          console.error(error);
        });
      }
    });
  };

  const googleLogin = (event) => {
    event.preventDefault();

    addToList(listOfTags);

    var provider = new auth_.GoogleAuthProvider();
    auth_().signInWithPopup(provider).then(function(result) {
      // The signed-in user info.
      var user = result.user;
      console.log(result.additionalUserInfo);
      if (result.additionalUserInfo.isNewUser) { //user has not signed in before
        console.log('new Google user');
        user.getIdToken(/* forceRefresh */ true).then(function(idToken) {
          const name = user.displayName.split(" ");
          const registrationInformation = {idToken, email: user.email, firstName: name[0], lastName: name[1]};
          register(registrationInformation);
          setRedirectToDashboard(true);
        }).catch(function(error) {
          console.error(error);
        });
      }
      else { //user has signed in before
        user.getIdToken(/* forceRefresh */ true).then(function(idToken) {
          login({idToken: idToken});
        }).catch(function(error) {
          console.error(error);
        });
      }
    }).catch(function(error) {
      console.error(error);
    });
  };

  /**
   * A reusable function to update the state with a key/value pair where the
   * key is the name of the component and the value is its most recent value...
   *
   * This is a great pattern to use if you need to make the UI react to the input
   * in more complex forms and if you need the most recent value of the users'
   * submission before they click submit for validation purposes...
   * @param name
   * @param value
   */
  const updateInput = ({ target: { name, value } }) => {
    setUserDetails(prevState => ({ ...prevState, [name]: value }));
  };

  if (redirectToRegister) {
    return <Redirect to="/registration" />;
  }

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <div className={classes.paper}>
        <Typography variant="h5">Login</Typography>
        <form className={classes.form} noValidate>
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            label="Email"
            name="email"
            type="email"
            autoFocus
            onChange={updateInput}
          />
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            label="Password"
            name="password"
            type="password"
            onChange={updateInput}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            className={classes.submit}
            onClick={attemptLogin}
            onChange={updateInput}
          >Sign in
          </Button>
        </form>
        <GoogleButton
          onClick={googleLogin}
        />
        <Link href="#" onClick={() => setRedirectToRegister(true)} variant="body2">
          Don't have an account? Register here
        </Link>
      </div>
    </Container>
  );
};

export default Login;