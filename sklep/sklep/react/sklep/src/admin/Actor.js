import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [actors, setActors] = useState([]);
	const history = useHistory();
	const [actor, setActor] = useState({name:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/actor/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const actors = await getRequest('http://localhost:9000/api/actor');
			setActors(actors);
		}
		a()
	}, [])

 return (	<div className="actors">
			{actors.map((actor, index) =>
			<div key={actor.id}>
				<h3><Link to={`/actor/${actor.id}`}>{actor.id}: {actor.name} {actor.surname}</Link></h3>
				<h3><a onClick={()=>{del(actor.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={actor}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/actor`, values, `POST`);
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
