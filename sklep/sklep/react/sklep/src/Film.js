import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [films, setFilms] = useState([]);
	const history = useHistory();
	const [film, setFilm] = useState({name:'', genre_id:'', director_id:'', actor_id:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/film/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const films = await getRequest('http://localhost:9000/api/film');
			setFilms(films);
		}
		a()
	}, [])

 return (	<div className="films">
			{films.map((film, index) =>
			<div key={film.id}>
				<h3><Link to={`/film/${film.id}`}>{film.id}: {film.name} {film.genre_id} {film.director_id}{film.actor_id}</Link></h3>
				<h3><a onClick={()=>{del(film.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={film}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/film`, values, `POST`);
	history.go(0)

       }}
     >

       <Form>

         <label htmlFor="name">name</label>
         <Field name="name" type="text" />
         <ErrorMessage name="name" />
         <br />
         <label htmlFor="genre_id">genre_id</label>
         <Field name="genre_id" type="number" />
         <ErrorMessage name="genre_id" />
         <br />
         <label htmlFor="director_id">director_id</label>
         <Field name="director_id" type="number" />
         <ErrorMessage name="director_id" />
         <br />
         <label htmlFor="actor_id">actor_id</label>
         <Field name="actor_id" type="number" />
         <ErrorMessage name="actor_id" />
         <br />
         <button type="submit">Submit</button>

       </Form>

     </Formik>
     </div>

   );

 };
