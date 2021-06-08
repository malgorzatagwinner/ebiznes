import React from "react";
import {Link} from 'react-router-dom';
import {getRequest} from "./utils";
export const Context = React.createContext();

const endpoint = 'http://localhost:9000/api';
const states ={
	films: {},
	directors: {},
	genre: {},
	reviews: {}
};
let loading = false;
const reducer = (state, action) => {
	switch (action.type){
		case 'setFilms':{
			console.log('setFilms to ', action.data);
			return {...state, films: action.data || {}};
		}
		case 'setDirs':{
			console.log('setDirs to ', action.data);
			return {...state, directors: action.data || {}};
		}
		case 'setGenres':{
			console.log('setGenres to ', action.data);
			return {...state, genres: action.data || {}};
		}
		case 'setReviews':{
			console.log('setReviews to ', action.data);
			return {...state, reviews: action.data || {}};
		}
		default: {
			throw new Error(`Unhandled action type: ${action.type}`)
		}
	}
};


const loadToMap = async (baseUrl, url) => {
	const all = await getRequest(`${baseUrl}/${url}`);
	return all.reduce((map, elem) => {
		map[elem.id] = elem;
		return map;
	}, {});
};
const loadToMap_Film = async (baseUrl, url) => {
	const all = await getRequest(`${baseUrl}/${url}`);
	return all.reduce((map, elem) => {
		if (map[elem.film_id]){
			map[elem.film_id].push(elem);
		}
		else{
			map[elem.film_id]=[elem];
		}
		return map;
	}, {});
};

export const StoreProvider = ({children}) => {
	const [store, storeDispatch] = React.useReducer(reducer, states);

	const customDispatch = async (action) => {
		switch(action.type) {
			case "loadAll": {
				customDispatch({type: 'loadFilms'});
				customDispatch({type: 'loadDirectors'});
				customDispatch({type: 'loadGenres'});
				customDispatch({type: 'loadReviews'});
				
				return;
			}
			case "loadFilms": {
				loadToMap(endpoint, 'film').then((data) => storeDispatch({type: 'setFilms', data}));
				return;
			}
			case "loadDirectors": {
				loadToMap(endpoint, 'director').then((data) => storeDispatch({type: 'setDirs', data}));
				return;
			}
			case "loadGenres": {
				loadToMap(endpoint, 'genre').then((data) => storeDispatch({type: 'setGenres', data}));
				return;
			}
			case "loadReviews": {
				loadToMap_Film(endpoint, 'review').then((data) => storeDispatch({type: 'setReviews', data}));
				return;
			}
			default: {
				storeDispatch(action);
			}
		}
	};

	return (
		<Context.Provider value={[store, customDispatch]}>
			{ children }
		</Context.Provider>
	);
};

export const useStore = () => {
	const context = React.useContext(Context);
	if (context === undefined) {
		throw new Error('useStore must be used within a StoreContext');
	}
	if (!loading && context[0] == states) {
		loading = true;
		context[1]({type: 'loadAll'});
	}
	return context;
};
