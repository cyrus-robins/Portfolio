import React from 'react';
import { BrowserRouter as Router, Redirect, Route, Switch } from 'react-router-dom';
import { ProfileProvider, useProfileProvider } from 'contexts/profile';
import HomePage from 'features/HomePage';
import Dashboard from 'features/Dashboard';
import ColonyPage from 'features/ColonyPage';
import Registration from 'features/Registration';
import Animals from './components/Animals';
import SingleAnimal from './components/SingleAnimal';
import AddAnimal from './components/AddAnimal';
import AdvancedSearch from './components/AdvancedSearch';
import ResultsPage from './components/ResultsPage';


/**
 * Renders a react-router enabled app with a wrapper to facilitate shared styles
 * and markup; add new routes for pages here.
 */
const App = () => (
  <Router>
    <ProfileProvider>
      <Switch>
        <ProtectedRoute exact path="/dashboard" component={Dashboard} />
        <ProtectedRoute path="/dashboard/colony" component={ColonyPage} />
        <ProtectedRoute path="/animals/:id" component={Animals} />
        <ProtectedRoute path="/animal/:id" component={SingleAnimal} />
        <ProtectedRoute path="/addanimal" component={AddAnimal} />
        <ProtectedRoute path="/advancedsearch" component={AdvancedSearch} />
        <ProtectedRoute path="/results" component={ResultsPage} />
        <Route exact path="/" component={HomePage} />
        <Route exact path="/registration" component={Registration} />
      </Switch>
    </ProfileProvider>
  </Router>
);


const ProtectedRoute = (props) => {
  const { state: { loggedIn } } = useProfileProvider();
  if (!loggedIn) return <Redirect to="/" />;
  return (<Route {...props} />);
};

export default App;
