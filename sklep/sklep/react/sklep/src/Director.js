import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [directors, setDirectors] = useState([]);
	const history = useHistory();
	const [director, setDirector] = useState({name:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/director/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const directors = await getRequest('http://localhost:9000/api/director');
			setDirectors(directors);
		}
		a()
	}, [])

 return (	<div className="directors">
			{directors.map((director, index) =>
			<div key={director.id}>
				<h3><Link to={`/director/${director.id}`}>{director.id}: {director.name} {director.surname}</Link></h3>
				<h3><a onClick={()=>{del(director.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={director}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/director`, values, `POST`);
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
