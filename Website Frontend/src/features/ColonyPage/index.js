import React, { useState } from 'react';
import { Redirect } from 'react-router-dom';
import Animals from 'components/Animals';
import { useProfileProvider } from 'contexts/profile';
import { Breadcrumbs, Link, makeStyles } from '@material-ui/core';
import NavigateNextIcon from '@material-ui/icons/NavigateNext';


const useStyles = makeStyles(theme => ({
  root: {
    '& > * + *': {
      marginTop: theme.spacing(2),
    },
  },
}));

const ColonyPage = () => {
  const { logout, state } = useProfileProvider();
  const [redirectToColonies, setRedirectToColonies] = useState(false);
  const [redirectToLogin, setRedirectToLogin] = useState(false);
  const { colonyName } = state;

  const classes = useStyles();

  if (redirectToColonies) {
    return <Redirect to="/dashboard" />;
  } else if (redirectToLogin) {
    logout();
    return <Redirect to="/" />;
  }

  return (
    <div className="animals">
      <div className={classes.root}>
        <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} aria-label="breadcrumb">
          <Link color="inherit" onClick={() => setRedirectToLogin(true)}>
            Logout
          </Link>
          <Link color="inherit" onClick={() => setRedirectToColonies(true)}>
            Colonies
          </Link>
          <Link
            color="textPrimary"
            aria-current="page"
          >
            {colonyName}
          </Link>
        </Breadcrumbs>
      </div>
      <Animals />
    </div>
  );
};

export default ColonyPage;
