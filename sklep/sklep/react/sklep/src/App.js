import './App.css';
import {HashRouter, Route, Switch} from 'react-router-dom';
import {StoreProvider} from './ProductsContext'
import {UserProvider} from './UserContext'
import ProductList from './ProductList'
import Header from './links'
import Welcome from './hello'
import ShoppingBag from './ShoppingBag'
import Favourite from './FavouriteList'
import SignUp from './SignUp'
import SignIn from './SignIn'

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
	<HashRouter>
	  <Header/>
	  <CustomSwitch>
		  <Route path="/products" component={ProductList}/>
		  <Route path="/shoppingBag" component={ShoppingBag}/>
		  <Route path="/favourite" component={Favourite}/>
		  <Route path="/signUp" component={SignUp}/>
		  <Route path="/signIn" component={SignIn}/>
		  <Route path="/" component={Welcome}/>
	  </CustomSwitch>
	</HashRouter>
      </UserProvider>
      </StoreProvider>
      </div>
      <div className="footer">
	Małgorzata Gwinner 2021	
      </div>
    </div>
  );
}

