import React, { useState } from 'react';
import { Redirect } from 'react-router-dom';
import { useProfileProvider } from 'contexts/profile';
import { Button, Breadcrumbs, Link, TextField, Grid, Container, CssBaseline, Typography } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import { auth } from 'components/FirebaseConfig';

const Register = () => {
  const { register } = useProfileProvider();
  const [userDetails, setUserDetails] = useState({});
  const [redirectToLogin, setRedirectToLogin] = useState(false);
  const [redirectToDashboard, setRedirectToDashboard] = useState(false);

  /** Material-UI */
  const useStyles = makeStyles(theme => ({
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
      marginTop: theme.spacing(3),
    },
    submit: {
      margin: theme.spacing(3, 0, 2),
    },
  }));

  const classes = useStyles();

  const attemptRegister = (event) => {
    // TODO: Check for 401 and redirect if 200.
    event.preventDefault();

    const { email, password } = userDetails;
    auth.createUserWithEmailAndPassword(email, password).catch(function(error) {
      // Handle Errors here.
      var errorCode = error.code;
      var errorMessage = error.message;
      if (errorCode === 'auth/weak-password') {
        alert('The password is too weak.');
      } else {
        alert(errorMessage);
      }
      console.log(error);
    });

    //TODO some reason this is still getting called when the above gets an error.... idk
    auth.onAuthStateChanged(function(user) {
      if (user) {
        user.getIdToken(/* forceRefresh */ true).then(function(idToken) {
          userDetails.idToken = idToken;
          register(userDetails);
          setRedirectToDashboard(true);
        }).catch(function(error) {
          console.error(error);
        });
      }
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

  if (redirectToLogin) {
    return <Redirect to="/" />;
  }

  if (redirectToDashboard) {
    return <Redirect to="/dashboard" />;
  }

  return (
    <div>
      <div style={{ textAlign: 'left' }}>

        <Breadcrumbs aria-label="breadcrumb">
          <Link color="inherit" onClick={() => setRedirectToLogin(true)}>
            Logout
          </Link>
          <Link
            color="textPrimary"
            aria-current="page"
          >
            Register
          </Link>
        </Breadcrumbs>
      </div>
      <Container component="main" maxWidth="xs">
        <CssBaseline />
        <div className={classes.paper}>
          <Typography component="h1" variant="h5">
            Register
          </Typography>

          <form className={classes.form} noValidate>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  name="firstName"
                  variant="outlined"
                  required
                  fullWidth
                  label="First Name"
                  autoFocus
                  onChange={updateInput}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  name="lastName"
                  variant="outlined"
                  required
                  fullWidth
                  label="Last Name"
                  onChange={updateInput}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  type="email"
                  name="email"
                  variant="outlined"
                  required
                  fullWidth
                  label="Email"
                  onChange={updateInput}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  type="password"
                  name="password"
                  variant="outlined"
                  required
                  fullWidth
                  label="Password"
                  onChange={updateInput}
                />
              </Grid>
            </Grid>

            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              className={classes.submit}
              onChange={updateInput}
              onClick={attemptRegister}
            >
              Register
            </Button>
          </form>
        </div>
      </Container >
    </div>
  );
};

export default Register;
