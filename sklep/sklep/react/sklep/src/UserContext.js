import React, {useState, useEffect} from "react";
import {Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';
export const Context = React.createContext();

const endpoint = 'http://localhost:9000/api';
const states ={
	favourites: {},
	orders: {},
	payments: {},
	shoppingBag: {},
};
let loading = false;

const reducer = (state, action) => {
	switch (action.type){
		case 'setFavourites':{
			console.log('setFavourites to ', action.data);
			return {...state, favourites: action.data || {}};
		}
		case 'setOrders':{
			console.log('setOrders to ', action.data);
			return {...state, orders: action.data || {}};
		}
		case 'setPayments':{
			console.log('setPayments to ', action.data);
			return {...state, payments: action.data || {}};
		}
		case 'setShoppingBag':{
			console.log('setShoppingBag to ', action.data);
			return {...state, shoppingBag: action.data || {}};
		}
		case 'addFilmtoBag':{
			console.log('addFilmtoBag to now have ', action.film);
			const newstate = {...state}
			if(!newstate.shoppingBag[action.film]){
				newstate.shoppingBag[action.film] = 1
			}else{ //tu dodaje dwa razy, dziwne...
				newstate.shoppingBag[action.film] += 1
			}
			return newstate;
		}
		case 'deleteFilmfromBag':{
			console.log('deleteFilmfromBag to now have ', action.film);
			const newstate = {...state}
			if(newstate.shoppingBag[action.film]>1){
				newstate.shoppingBag[action.film] -= 1
			}
			else{
				delete newstate.shoppingBag[action.film]
			}
			return newstate;
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


export const UserProvider = ({children}) => {
	const [user, userDispatch] = React.useReducer(reducer, states);

	const customDispatch = async (action) => {
		switch(action.type) {
			case "loadAll": {
				customDispatch({type: 'loadFavourites'});
				customDispatch({type: 'loadOrders'});
				customDispatch({type: 'loadPayments'});
			//	customDispatch({type: 'loadShoppingBag'});
				
				return;
			}
			case "loadFavourites": {
				loadToMap(endpoint, 'favourite').then((data) => userDispatch({type: 'setFavourites', data}));
				return;
			}
			case "loadOrders": {
				loadToMap(endpoint, 'order').then((data) => userDispatch({type: 'setOrders', data}));
				return;
			}
			case "loadPayments": {
				loadToMap(endpoint, 'payment').then((data) => userDispatch({type: 'setPayments', data}));
				return;
			}
			case "loadShoppingBag": {
				loadToMap(endpoint, 'shoppingBag').then((data) => userDispatch({type: 'setShoppingBag', data}));
				return;
			}
			default: {
				userDispatch(action);
				return;
			}
		}
	};

	return (
		<Context.Provider value={[user, customDispatch]}>
			{ children }
		</Context.Provider>
	);
};

export const useUser = () => {
	const context = React.useContext(Context);
	if (context === undefined) {
		throw new Error('useUser must be used within a UserContext');
	}
	if (!loading && context[0] == states) {
		loading = true;
		context[1]({type: 'loadAll'});
	}
	return context;
};
