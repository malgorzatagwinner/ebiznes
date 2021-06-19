import React from 'react';
import { useStore } from './ProductsContext';
import { useUser } from './UserContext';

export default () => {
	const [store, ] = useStore();
	const { films} = store;

	const[{shoppingBag}, userDispatch] = useUser();
	
	const deletefromBag = (id) => {
		userDispatch({type: 'deleteFilmfromBag', film:id});
	}
	console.log(shoppingBag)	
	return (<ul className="lista">
		{
			Object.entries(shoppingBag).map(([item, index]) => (
				<li key={ item }>
					{
						films?.[item]?.name 
					} <br/>
					<a onClick={() => deletefromBag(films?.[item]?.id)}>
						End rent
						</a>
				</li>
			))
		}
	</ul>);
};
