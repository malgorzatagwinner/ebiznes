import React from 'react';
import { useStore } from './ProductsContext';
import { useUser } from './UserContext';

export default () => {
	const [store, storeDispatch] = useStore();
	const { films, directors, genres, reviews } = store;

	const[user, userDispatch] = useUser();
	const { shoppingBag } = user;
	
	const deletefromBag = (id) => {
		userDispatch({type: 'deleteFilmfromBag', film:id});
	}
	
	return (<ul>
		{
			Object.entries(shoppingBag).map(([item, index]) => (
				<li key={ item }>
					{
						films?.[item]?.name 
					} {index}
					<a onClick={() => deletefromBag(films?.[item]?.id)}>
						Delete one item
						</a>
				</li>
			))
		}
	</ul>);
};
