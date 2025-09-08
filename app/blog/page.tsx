import Link from 'next/link';

const posts = [
  { slug: 'primer-caso', title: 'Primer Caso Destacado' }
];

export const metadata = {
  title: 'Blog - Carrillo Abogados',
  description: 'Artículos y casos de interés legal'
};

export default function BlogPage() {
  return (
    <main>
      <h1>Blog</h1>
      <ul>
        {posts.map(post => (
          <li key={post.slug}>
            <Link href={`/blog/${post.slug}`}>{post.title}</Link>
          </li>
        ))}
      </ul>
    </main>
  );
}
