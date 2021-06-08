import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [reviews, setReviews] = useState([]);
	const history = useHistory();
	const [review, setReview] = useState({stars:'', txt:'',user_id:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/review/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const reviews = await getRequest('http://localhost:9000/api/review');
			setReviews(reviews);
		}
		a()
	}, [])

 return (	<div className="reviews">
			{reviews.map((review, index) =>
			<div key={review.id}>
				<h3><Link to={`/review/${review.id}`}>{review.id}: {review.stars} {review.txt}{review.user_id}</Link></h3>
				<h3><a onClick={()=>{del(review.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={review}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/review`, values, `POST`);
	history.go(0)

       }}
     >

       <Form>

         <label htmlFor="stars">stars</label>
         <Field name="stars" type="number" />
         <ErrorMessage name="stars" />
         <br />
         <label htmlFor="txt">txt</label>
         <Field name="txt" type="text" />
         <ErrorMessage name="txt" />
         <br />
         <label htmlFor="user_id">user_id</label>
         <Field name="user_id" type="number" />
         <ErrorMessage name="user_id" />
         <br />
         <button type="submit">Submit</button>

       </Form>

     </Formik>
     </div>

   );

 };
