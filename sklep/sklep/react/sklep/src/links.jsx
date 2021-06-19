import React from 'react';
import {Link} from 'react-router-dom';
import ShoppingBag from './ShoppingBag';
import {useUser} from './UserContext'


export default function(props){
	const [{user},dispatchUser] = useUser();

	const Buttons = () => {
		if(user?.email){
			return (<>
	    <li><Link to ="/shoppingBag">Rented films</Link> </li>
	    <li><Link to ="/favourite">Your favourites</Link> </li>
	    <li><a onClick={signOut}  > Sign Out</a> </li>
				</>
			)
		}
		return (
			<>
	    <li><Link to ="/signUp">Sign Up</Link> </li>
	    <li><Link to ="/signIn">Sign In</Link> </li>
		</>
		)
	}

	const signOut = () =>{
		dispatchUser({type: 'signOut'})
	}

	return(<>
	  <ul className="menu">
	    <li><Link to ="/products">Products</Link> </li>
	<Buttons/>
	  </ul>
	  </>);
}
