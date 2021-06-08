import React from 'react';
import { useStore } from './ProductsContext';
import { useUser } from './UserContext';

export default () => {
	const [store, ] = useStore();
	const { films, directors, genres, reviews } = store;

	const[, userDispatch] = useUser();

	const addtoBag = (id) => {
		userDispatch({type: 'addFilmtoBag', film:id});
	}
	
	return (<ul>
		{
			Object.values(films).map((film) => (
				<li key={ film.id }>
					{
						film.id
					}. {
						film.name
					} by {
						directors?.[film.director_id]?.name || <i>directors #{film.director_id}</i>
					} - {
						genres?.[film.genre_id]?.name || <i>genre #{films.genre_id}</i>
					}
					{
						reviews?.[film.id]?.map((review)=>(
						<i key={review.id}>{ review.txt} </i>
						))
					}
						<a onClick={() => addtoBag(film.id)}>
						Add to Shopping Bag
						</a>
				</li>
			))
		}
	</ul>);
};
