import React from 'react';
import { useProfileProvider } from 'contexts/profile';
import { Redirect } from 'react-router-dom';
import Login from 'components/LoginForm';
import { Typography } from '@material-ui/core';


const HomePage = () => {
  const { state: { loggedIn } } = useProfileProvider();
  return (
    <div className="home-page" style={{ textAlign: 'center' }}>
      <div style={{ paddingTop: 75 }}>
        <Typography variant="h5">
          Animal Colony Management System
        </Typography>
      </div>
      { loggedIn ? <Redirect to="/dashboard" /> : <Login /> }
    </div>
  );
};

export default HomePage;
