import React from 'react';
import { useStore } from './ProductsContext';
import { useUser } from './UserContext';

export default () => {
	const [store, ] = useStore();
	const { films, directors, genres, reviews } = store;

	const[{user}, userDispatch] = useUser();

	const addtoFav = (id) => {
		userDispatch({type: 'addFilmtoFav', film:id});
	}
	const addtoBag = (id) => {
		userDispatch({type: 'addFilmtoBag', film:id});
	}
	const RentButton = ({id}) => {
			return <a onClick={() => addtoBag(id)}>
						Rent a film
						</a>
	}

	return (<ul className="lista">
		{
			Object.values(films).map((film) => (
				<li key={ film.id }>
					{
						film.name
					}<br/> by {
						directors?.[film.director_id]?.name || <i>directors #{film.director_id}</i>
					}<br/> - <br/>{
						genres?.[film.genre_id]?.name || <i>genre #{films.genre_id}</i>
					}
				{user?.email && <><br/>
					<RentButton id={film.id}/>	
					<br/>	<a onClick={() => addtoFav(film.id)}>
						Add to your favourites
						</a></>}
				</li>
			))
		}
	</ul>);
};
