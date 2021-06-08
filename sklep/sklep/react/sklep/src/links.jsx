import React from 'react';
import {Link} from 'react-router-dom';
import ShoppingBag from './ShoppingBag';

export default function(props){
	  return(<>
	  <ul>
	    <li><Link to ="/products">Products</Link> </li>
	    <li><Link to ="/favourites">Favourites</Link> </li>
	  </ul>
	  <ShoppingBag/>
	  </>);
}
