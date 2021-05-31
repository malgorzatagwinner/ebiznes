import React from 'react';
import { useStore } from './ProductsContext';

export default () => {
	const [store, storeDispatch] = useStore();
	const { films, directors, genres } = store;

	console.log(store);
	return (<ul>
		{
			Object.values(films).map((films) => (
				<li key={ films.id }>
					{
						films.id
					}. {
						films.name
					} by {
						directors?.[films.director_id]?.name || <i>directors #{films.director_id}</i>
					} - {
						genres?.[films.genre_id]?.name || <i>genre #{films.genre_id}</i>
					}
				</li>
			))
		}
	</ul>);
};
