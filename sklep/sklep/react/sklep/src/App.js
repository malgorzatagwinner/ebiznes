import logo from './logo.svg';
import './App.css';
import {BrowserRouter, Link, Route, Switch} from 'react-router-dom';
import {StoreProvider} from './ProductsContext'
import {UserProvider} from './UserContext'
import ProductList from './ProductList'
import Product from './Product'
import Header from './links'
import Welcome from './hello'
import FavouriteList from './FavouriteList'

const NotFound = () => (
		<h1>Not Found</h1>
);

const CustomSwitch = ({children}) => (
	<Switch>
		{children}
		<Route>
			<NotFound />
		</Route>
	</Switch>
);

export default function App() {
  return (
  <div className="App">
    <div>
      <StoreProvider>
      <UserProvider>
	<BrowserRouter>
	  <Header/>
	  <CustomSwitch>
		  <Route path="/products" component={ProductList}/>
		  <Route path="/favourites" component={FavouriteList}/>
		  <Route path="/" component={Welcome}/>
	  </CustomSwitch>
	</BrowserRouter>
      </UserProvider>
      </StoreProvider>
      </div>
    </div>
  );
}

