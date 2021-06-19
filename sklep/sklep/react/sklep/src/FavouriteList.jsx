import React from 'react';
import { useStore } from './ProductsContext';
import { useUser } from './UserContext';

export default () => {
	const [user, userDispatch] = useUser();
	const { favourites} = user;
	const [store, storeDispatch] = useStore();
	const { films, directors, genres, reviews, actors } = store;

	const addtoBag = (id) => {
		userDispatch({type: 'addFilmtoBag', film:id});
	}
	
	const deletefromFav = (id) => {
		userDispatch({type: 'deleteFilmfromFav', film:id});
	}
	
	
	return (<ul className="lista">
                {
                        Object.entries(favourites).map(([item, index]) => {
				const film = films?.[item];
			return	(
                                <li key={ film.id }>
                                        {
                                                film.name 
                                        } <br/> by {
						directors?.[film.director_id]?.name || <i>directors #{film.director_id}</i>
					} with {
						actors?.[film.actor_id]?.name || <i> actors # {film.actor_id}</i>
					
					}
                                        <br/>
					<a onClick={() => addtoBag(film.id)}>
					Add to Shopping Bag
					</a>
					<br/>
					<a onClick={() => deletefromFav(film.id)}>
					Delete from favourites
					</a>

                                </li>
                        )
			})
                }
        </ul>);

};
