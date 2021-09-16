import React, { useState } from 'react';
import Colonies from 'components/Colonies';
import { Redirect } from 'react-router-dom';
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

const Dashboard = () => {
  const { logout } = useProfileProvider();
  const [redirectToLogin, setRedirectToLogin] = useState(false);

  const classes = useStyles();

  if (redirectToLogin) {
    logout();
    return <Redirect to="/" />;
  }

  return (
    <div className="dashboard">
      <div className={classes.root} style={{ textAlign: 'left' }}>
        <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} aria-label="breadcrumb">
          <Link color="inherit" onClick={() => setRedirectToLogin(true)}>
            Logout
          </Link>
          <Link
            color="textPrimary"
            aria-current="page"
          >
            Colonies
          </Link>
        </Breadcrumbs>
      </div>
      <Colonies />
    </div>
  );
};

export default Dashboard;
