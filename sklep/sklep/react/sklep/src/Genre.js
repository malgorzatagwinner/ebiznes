import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [genres, setGenres] = useState([]);
	const history = useHistory();
	const [genre, setGenre] = useState({name:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/genre/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const genres = await getRequest('http://localhost:9000/api/genre');
			setGenres(genres);
		}
		a()
	}, [])

 return (	<div className="genres">
			{genres.map((genre, index) =>
			<div key={genre.id}>
				<h3><Link to={`/genre/${genre.id}`}>{genre.id}: {genre.name} {genre.surname}</Link></h3>
				<h3><a onClick={()=>{del(genre.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={genre}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/genre`, values, `POST`);
	history.go(0)

       }}
     >

       <Form>


         <label htmlFor="name">Name</label>
         <Field name="name" type="text" />
         <ErrorMessage name="name" />
         <br />   
	 <button type="submit">Submit</button>
	 
       </Form>

     </Formik>
     </div>

   );

 };
