import logo from './logo.svg';
import './App.css';
import {BrowserRouter, Link, Route, Switch} from 'react-router-dom';
import {StoreProvider} from './ProductsContext'
import ProductList from './ProductList'
import Product from './Product'

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
	<BrowserRouter>
	  <ul>
	    <li><Link to ="/products">Products</Link> </li>
	  </ul>
	  <CustomSwitch>
		  <Route path="/products" component={ProductList}/>
	  </CustomSwitch>
	</BrowserRouter>
      </StoreProvider>
      </div>
    </div>
  );
}

