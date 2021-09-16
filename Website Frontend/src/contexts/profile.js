import axios from 'axios';
import PropTypes from 'prop-types';
import React, { createContext, useReducer, useContext } from 'react';

const initialState = { loggedIn: false, name: {} };
const store = createContext(initialState);

const { Provider } = store;

const BASE_URL = 'http://localhost:5000/api';

const LOGIN = 'LOGIN';
const LOGOUT = 'LOGOUT';
const REGISTER = 'REGISTER';
const COLONY = 'COLONY';
const ANIMALS = 'ANIMALS';
const SORT = 'SORT';
const ALPHASORT = 'ALPHASORT';
const DELETE = 'DELETE';
const EDITANIMAL = 'EDITANIMAL';
const ADDANIMAL = 'ADDANIMAL';
const DELETEANIMAL = 'DELETEANIMAL';
const IMAGEUPLOAD = 'IMAGEUPLOAD';
const IMAGEDELETE = 'IMAGEDELETE';
const NOTE = 'NOTE';
const EVENT = 'EVENT';
const TAG = 'TAG';
const TAGS = 'TAGS';
const SEARCH = 'SEARCH';

axios.defaults.withCredentials = true;

const ProfileProvider = ({ children }) => {
  const [state, dispatch] = useReducer((prevState, action) => {
    const { type, payload } = action;

    switch (type) {
      case LOGIN: {
        // Store the profile data in the state
        return { ...prevState, loggedIn: true, ...payload };
      }

      case REGISTER: {
        // Store registered profile in the state
        return { ...prevState, loggedIn: true, ...payload };
      }

      case COLONY: {
        const colonies = [payload, ...prevState.ownedColonies];
        return { ...prevState, ownedColonies: colonies };
      }

      case DELETE: {
        const colonies = payload.sharedTable ? [...prevState.sharedColonies] : [...prevState.ownedColonies];
        const newList = colonies.filter((item, index) => item.colonyId !== payload.colonyId);
        return payload.sharedTable ? { ...prevState, sharedColonies: newList } : { ...prevState, ownedColonies: newList };
      }

      case DELETEANIMAL: {
        const animals = [...prevState.animals];
        const newList = animals.filter((item, index) => item.animalUUID !== payload);
        return { ...prevState, animals: newList };
      }

      case SEARCH: {
        return {...prevState, searchAnimals: payload};
      }

      case SORT: {
        const colonies = [...prevState.ownedColonies];
        colonies.sort((a, b) => {
          if (a[payload] < b[payload]) {
            return -1; // locations swap
          }
          if (a[payload] > b[payload]) {
            return 1;
          }
          return 0;
        });
        return { ...prevState, ownedColonies: colonies };
      }

      case ALPHASORT: {
        const colonies = [...prevState.ownedColonies];
        colonies.sort((a, b) => {
          if (a[payload].toLowerCase() < b[payload].toLowerCase()) {
            return -1;
          }
          if (a[payload].toLowerCase() > b[payload].toLowerCase()) {
            return 1;
          }
          return 0;
        });
        return { ...prevState, ownedColonies: colonies };
      }

      case ANIMALS: {
        // Store colony animals in the state
        return {
          ...prevState, colonyId: payload.colonyId, accessRights: payload.accessRights, colonyName: payload.colonyName, colonySize: payload.colonySize, geneNames: payload.geneNames, animals: payload.animals,
        };
      }

      case EDITANIMAL: {
        const animals = [...prevState.animals];
        const targetIndex = animals.findIndex(item => item.animalUUID === payload.animalUUID);
        // Get index of animal to edit
        if (targetIndex !== -1) {
          animals[targetIndex] = payload; // Store edited animal
        }
        return {
          ...prevState, animals,
        };
      }

      //TODO is this even right?
      case ADDANIMAL: {
        const animals = [...prevState.animals];
        const newList = animals.concat(payload);
        return { ...prevState, animals: newList };
      }

      case IMAGEUPLOAD: {
        const animals = [...prevState.animals];
        const targetIndex = animals.findIndex(item => item.animalUUID === payload.animalId);
        console.log("adding image to animal", payload.imageArray);
        // Get index of animal to edit
        if (targetIndex !== -1) {
          console.log("animal links:", animals[targetIndex].imageLinks);
          animals[targetIndex].imageLinks.push(payload.imageArray); // Store edited animal
        }
        console.log("added image to animal");
        return {
          ...prevState, animals,
        };
      }

      case IMAGEDELETE: {
        const animals = [...prevState.animals];
        const targetIndex = animals.findIndex(item => item.animalUUID === payload.animalId);
       
        const image = payload.imageObject;

        console.log("target animal:", targetIndex);
        console.log("target image:", image);

        // Get index of animal to edit
        if (targetIndex !== -1) {
          const imageIndex = animals[targetIndex].imageLinks.findIndex(elem => {
            if(elem.url === image.url && elem.note === image.note
              && elem.timestamp === image.timestamp && elem.date === image.date){
                return true;
              }
              return false;
          });
          
          console.log("imageindex", imageIndex);
          if(imageIndex !== -1){
            console.log("before splice", animals[targetIndex].imageLinks);

            animals[targetIndex].imageLinks.splice(imageIndex, 1);;

            console.log("after splice", animals[targetIndex].imageLinks);
          }
        }
        return {
          ...prevState, animals,
        };
      }

      case NOTE: {
        const animals = [...prevState.animals];
        const targetIndex = animals.findIndex(item => item.animalUUID === payload.animalId);
        // Get index of animal to edit
        if (targetIndex !== -1) {
          console.log("push note: ", payload.note);
          animals[targetIndex].notes.push(payload.note); // Store edited animal
        }
        return {
          ...prevState, animals,
        };
      }

      case EVENT: {
        const animals = [...prevState.animals];
        const targetIndex = animals.findIndex(item => item.animalUUID === payload.animalId);
        // Get index of animal to edit
        if (targetIndex !== -1) {
          animals[targetIndex].events.push(payload.eventInfo); // Store edited animal
        }
        return {
          ...prevState, animals,
        };
      }

      case TAG: {
        return { ...prevState, tagName: payload.tagName, mouseList: payload.mouseList,};
      }

      case TAGS: {
        return { ...prevState, listOfTags: payload.tagList,};
      }

      case LOGOUT: {
        // Reset state to logged out
        return initialState;
      }
      default:
        throw new Error();
    }
  }, initialState);

  return <Provider value={{ state, dispatch }}>{children}</Provider>;
};

const useProfileProvider = () => {
  const { state, dispatch } = useContext(store);

  const login = credentials => axios
    .post(`${BASE_URL}/login`, credentials)
    .catch(function(response) {
      console.log(response.data.status);
    })
    .then(({ data }) => {
      dispatch({ type: LOGIN, payload: data });
    });

  const register = credentials => axios
    .post(`${BASE_URL}/user`, credentials)
    .then(({ data }) => {
      dispatch({ type: REGISTER, payload: data });
    });

  const logout = () => dispatch({
    type: LOGOUT,
  });

  const addColony = newColony => axios
    .post(`${BASE_URL}/colony`, newColony)
    .then(({ data }) => {
      dispatch({ type: COLONY, payload: data });
      console.log(data);
      return data;
    });

  const shareColony = shareInfo => axios
    .post(`${BASE_URL}/colony/share`, shareInfo);

  const getAnimals = async (pageInfo, accessRights, colonyName, colonySize, geneNames) => axios
    .post(`${BASE_URL}/animals`, pageInfo)
    .then(({ data }) => {
      console.log(data);
      data.accessRights = accessRights;
      data.colonyName = colonyName;
      data.colonySize = colonySize;
      data.geneNames = geneNames ? geneNames : {gene1: 'Gene 1', gene2: 'Gene 2', gene3: 'Gene 3'};
      dispatch({ type: ANIMALS, payload: data });
    });

  const searchAnimals = async (searchInfo) => axios
    .post(`${BASE_URL}/animals/search`, searchInfo)
    .then(({ data }) => {
      console.log(data);
      return data;
  });

  const deleteColony = (colonyId, sharedTable) => axios
    .post(`${BASE_URL}/colony/delete`, { colonyId }) // passing colony id to the colony id object
    .then(() => {
      dispatch({ type: DELETE, payload: { colonyId, sharedTable } });
    });

  const deleteAnimal = request => axios
    .post(`${BASE_URL}/animals/delete`, request)
    .then(() => {
      dispatch({ type: DELETEANIMAL, payload: request.animalId });
    });

  const editAnimal = request => axios
    .post(`${BASE_URL}/animals/edit`, request)
    .then(({ data }) => {
      dispatch({ type: EDITANIMAL, payload: data });
    });

  const addAnimal = request => axios
    .post(`${BASE_URL}/animals/add`, request)
    .then(({ data }) => {
      dispatch({ type: ADDANIMAL, payload: data });
    });

  const storeImageLink = request => axios
    .post(`${BASE_URL}/animals/storeImageLink`, request)
    .then(({ data }) => {
      console.log("data:", data);
      dispatch({ type: IMAGEUPLOAD, payload: data });
      console.log("called dispatch");
    });

/*
    const deleteAnimal = request => axios
    .post(`${BASE_URL}/animals/delete`, request)
    .then(() => {
      dispatch({ type: DELETEANIMAL, payload: request.animalId });
    });
*/
  const deleteImageLink = (request) => axios
    .post(`${BASE_URL}/animals/deleteImageLink`, request)
    .then(() => {
      // console.log("data in delete:", data);
      dispatch({ type: IMAGEDELETE, payload: request });
      console.log("called dispatch on delete");
    });

  const storeNote = request => axios
    .post(`${BASE_URL}/animals/storeNote`, request)
    .then(({ data }) => {
      dispatch({ type: NOTE, payload: data });
    });

  const storeEvent = request => axios
    .post(`${BASE_URL}/animals/storeEvent`, request)
    .then(({ data }) => {
      dispatch({ type: EVENT, payload: data });
    });

  const getTag = request => axios
    .post(`${BASE_URL}/tags/getTag`, request)
    .then(({ data }) => {
      dispatch({ type: TAG, payload: data});
    });

  const getAllTags = request => axios
    .post(`${BASE_URL}/tags/getAllTags`, request)
    .then(({ data }) => {
      dispatch({ type: TAGS, payload: data});
    });

  const createTag = request => axios
    .post(`${BASE_URL}/tags/createTag`, request)
    .then(({ data }) => {
      dispatch({ type: TAG, payload: data});
    });

  const addNewToTag = request => axios
  .post(`${BASE_URL}/tags/addNewToTag`, request)
  .then(({ data }) => {
    dispatch({ type: TAG, payload: data});
  });

  const sortList = (sortBy) => {
    dispatch({ type: SORT, payload: sortBy });
  };

  const sortAlpha = (sortBy) => {
    dispatch({ type: ALPHASORT, payload: sortBy });
  };

  return {
    state,
    dispatch,
    login,
    logout,
    register,
    addColony,
    getAnimals,
    searchAnimals,
    sortList,
    sortAlpha,
    deleteColony,
    deleteAnimal,
    editAnimal,
    addAnimal,
    shareColony,
    storeImageLink,
    deleteImageLink,
    storeNote,
    storeEvent,
    getTag,
    getAllTags,
    createTag,
    addNewToTag,
  };
};

ProfileProvider.propTypes = {
  children: PropTypes.node.isRequired,
};

export { ProfileProvider, useProfileProvider };
