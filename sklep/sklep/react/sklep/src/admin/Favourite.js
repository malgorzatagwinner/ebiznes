import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [favourites, setFavourites] = useState([]);
	const history = useHistory();
	const [favourite, setFavourite] = useState({ film_id:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/favourite/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const favourites = await getRequest('http://localhost:9000/api/favourite');
			setFavourites(favourites);
		}
		a()
	}, [])

 return (	<div className="favourites">
			{favourites.map((favourite, index) =>
			<div key={favourite.id}>
				<h3><Link to={`/favourite/${favourite.id}`}>{favourite.id}: {favourite.film_id}</Link></h3>
				<h3><a onClick={()=>{del(favourite.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={favourite}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/favourite`, values, `POST`);
	history.go(0)

       }}
     >

       <Form>

         <label htmlFor="film_id">film_id</label>
         <Field name="film_id" type="number" />
         <ErrorMessage name="film_id" />
         <br />
         <button type="submit">Submit</button>

       </Form>

     </Formik>
     </div>

   );

 };
