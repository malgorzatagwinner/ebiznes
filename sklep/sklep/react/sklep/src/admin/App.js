import logo from './logo.svg';
import './App.css';
import {BrowserRouter, Link, Route, Switch} from 'react-router-dom';
import User from './User'
import UserEdit from './UserEdit'
import Film from './Film'
import FilmEdit from './FilmEdit'
import Favourite from './Favourite'
import FavouriteEdit from './FavouriteEdit'
import Director from './Director'
import DirectorEdit from './DirectorEdit'
import Genre from './Genre'
import GenreEdit from './GenreEdit'
import Payment from './Payment'
import PaymentEdit from './PaymentEdit'
import Actor from './Actor'
import ActorEdit from './ActorEdit'
import Order from './Order'
import OrderEdit from './OrderEdit'
import Review from './Review'
import ReviewEdit from './ReviewEdit'
import ShoppingBag from './ShoppingBag'
import ShoppingBagEdit from './ShoppingBagEdit'

export default function App() {
  return (
    <div className="App">
      <div>
	<BrowserRouter>
	  <ul>
	    <li><Link to ="/user">Users</Link> </li>
	    <li><Link to ="/film">Films</Link> </li>
	    <li><Link to ="/favourite">Favourites</Link> </li>
	    <li><Link to ="/genre">Genres</Link> </li>
	    <li><Link to ="/director">directors</Link> </li>
	    <li><Link to ="/actor">actors</Link> </li>
	    <li><Link to ="/order">orders</Link> </li>
	    <li><Link to ="/payment">payments</Link> </li>
	    <li><Link to ="/shoppingBag">shoppingBags</Link> </li>
	    <li><Link to ="/review">reviews</Link> </li>
	  </ul>
	  <Switch>
		  <Route path="/user/:id" component={UserEdit}/>
		  <Route path="/user" component={User}/>
		  <Route path="/film/:id" component={FilmEdit}/>
		  <Route path="/film" component={Film}/>
		  <Route path="/favourite/:id" component={FavouriteEdit}/>
		  <Route path="/favourite" component={Favourite}/>
		  <Route path="/genre/:id" component={GenreEdit}/>
		  <Route path="/genre" component={Genre}/>
		  <Route path="/director/:id" component={DirectorEdit}/>
		  <Route path="/director" component={Director}/>
		  <Route path="/actor/:id" component={ActorEdit}/>
		  <Route path="/actor" component={Actor}/>
		  <Route path="/order/:id" component={OrderEdit}/>
		  <Route path="/order" component={Order}/>
		  <Route path="/payment/:id" component={PaymentEdit}/>
		  <Route path="/payment" component={Payment}/>
		  <Route path="/shoppingBag/:id" component={ShoppingBagEdit}/>
		  <Route path="/shoppingBag" component={ShoppingBag}/>
		  <Route path="/review/:id" component={ReviewEdit}/>
		  <Route path="/review" component={Review}/>
	  </Switch>
	</BrowserRouter>
      </div>
    </div>
  );
}

