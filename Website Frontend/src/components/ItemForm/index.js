import React, { useState } from 'react';
import { useProfileProvider } from 'contexts/profile';
import { Button, TextField, Grid, Container, CssBaseline, Typography } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';

const ItemForm = () => {
  const { addItem } = useProfileProvider();
  const [itemDetails, setItemDetails] = useState({});

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

  const addToCart = (event) => {
    event.preventDefault();
    addItem(itemDetails);
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
    setItemDetails(prevState => ({ ...prevState, [name]: value }));
  };

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <div className={classes.paper}>

        <Typography component="h1" variant="h5">
          Add Item
        </Typography>

        <form className={classes.form} noValidate>

          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                name="name"
                variant="outlined"
                required
                fullWidth
                label="Item Name"
                autoFocus
                onChange={updateInput}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                name="cost"
                variant="outlined"
                required
                fullWidth
                label="Price"
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
            onClick={addToCart}
          >
            Add to Cart
          </Button>

        </form>

      </div>
    </Container >
  );
};

export default ItemForm;
