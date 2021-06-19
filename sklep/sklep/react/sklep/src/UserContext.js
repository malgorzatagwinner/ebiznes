import Cookies from "js-cookie";
import React from "react";
import {getRequest, endpoint} from "./utils";
export const Context = React.createContext();

const states ={
	favourites: {},
	orders: {},
	payments: {},
	shoppingBag: {},
	user:{}
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
		case 'addFilmtoFav':{
			console.log('addFilmtoBag to now have ', action.film);
			const newstate = {...state}
			if(!newstate.favourites[action.film]){
				newstate.favourites[action.film] = 1
			}
			console.log(newstate.favourites);
			return newstate;
		}
		case 'deleteFilmfromFav':{
			console.log('deleteFilmfromBag to now have ', action.film);
			const newstate = {...state}
			if(newstate.favourites[action.film]){
				delete newstate.favourites[action.film]
			}
			console.log(newstate.favourites);
			return newstate;
		}
		case 'addFilmtoBag':{
			console.log('addFilmtoBag to now have ', action.film);
			const newstate = {...state}
			if(!newstate.shoppingBag[action.film]){
				newstate.shoppingBag[action.film] = 1
			}
			console.log(newstate.shoppingBag);
			return newstate;
		}
		case 'deleteFilmfromBag':{
			console.log('deleteFilmfromBag to now have ', action.film);
			const newstate = {...state}
			delete newstate.shoppingBag[action.film]
			console.log(newstate.shoppingBag);
			return newstate;
		}
		case 'setUser':{
			console.log('setUser to now have ', action.data);
			return {...state, user: action.data || {}};

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
			case "signUp": {
				return getRequest(endpoint+'/signUp', action.user, 'POST').then((data) => {
					if(data && data.error){
						throw data;
					}
					if(data && data.success === false){
						throw {error: data.message || 'error'}
					}
				});
				
			}
			case "signIn": {
				return getRequest(endpoint+'/signIn', action.user, 'POST').then((data) => {
					if(data && data.error){
						throw data;
					}
					if(data && data.success === false){
						throw {error: data.message || 'error'}
					}
					userDispatch({type: 'setUser', data});
				});
				
			}
			case "signOut": {
				return getRequest(endpoint+'/signOut').then((data) => {
					userDispatch({type: 'setUser', data:{}});
					if(data && data.error){
						throw data;
					}
					if(data && data.success === false){
						throw {error: data.message || 'error'}
					}
				});
				
			}
			default: {
				userDispatch(action);
				return;
			}
		}
	};
	React.useEffect(() =>{
		const user = Cookies.get("user");
		if(!user)
			return
		Cookies.remove("user")
		userDispatch({type: "setUser", data:{email:user}});
	},[])
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
