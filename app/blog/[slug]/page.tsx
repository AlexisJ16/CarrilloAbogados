import { notFound } from 'next/navigation';

interface Params { params: { slug: string } }

const posts: Record<string, { title: string; content: string }> = {
  'primer-caso': {
    title: 'Primer Caso Destacado',
    content: 'Detalle del primer caso.'
  }
};

export default function BlogPost({ params }: Params) {
  const post = posts[params.slug];
  if (!post) return notFound();
  return (
    <main>
      <h1>{post.title}</h1>
      <p>{post.content}</p>
    </main>
  );
}
